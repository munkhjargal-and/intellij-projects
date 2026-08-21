# Frontend Code Organization Guide

Your app works now. This guide is about making the code *easy to live with* — so that adding a feature or fixing a bug is fast and safe.

## The problem

Look at the sizes of your three page files:

| File | Lines | useState | useForm | fetch calls |
|---|---|---|---|---|
| `src/VendorList.jsx` | 367 | 12 | 4 | 4 |
| `src/BottleList.jsx` | 365 | 12 | 4 | 5 |
| `src/BoxList.jsx` | 250 | 8 | 3 | 5 |

Each file does **five jobs at once**: fetching data, holding list state, holding form state, holding modal state, and drawing the screen. And the three files are mostly the **same code copied three times** — that's why the same bug (`result.data`) existed in all three pages, and why you fixed it on one page and left it on the other two.

**The fix is not a library. The fix is splitting one big file into several small ones, each with one job.**

## The target structure

```
src/
  constants.ts              ← you already have this
  api.js                    ← every fetch call lives here
  hooks/
    useVendors.js           ← vendors: data + loading + sorting + pagination + CRUD
    useBottles.js
    useBoxes.js
  components/
    Modal.jsx               ← your existing modal
    VendorForm.jsx          ← the create/edit form for a vendor
    BottleForm.jsx
    BoxForm.jsx
  pages/
    VendorList.jsx          ← now only "what to draw"
    BottleList.jsx
    BoxList.jsx
    VendorDetail.jsx
  App.jsx, NavBar.jsx, Home.jsx, NotFound.jsx
```

Read the whole guide first, then do the steps in order. After each step, run `npm run dev` and make sure the page still works.

---

## Step 1 — `api.js`: put every fetch in one file

**Why:** right now every page writes its own `fetch(...).then(response => response.json())` chain, and the three copies don't even agree (one reads `result`, another `result.data`, one crashes on deletes). When the API changes, you should change **one** file, not three. When a fetch is broken, you should find it in **one** place.

Create `src/api.js` with this content. Note the small `request` helper at the top: it does the fetch, throws on error responses, and **does not try to parse an empty body** (that's the delete bug — `DELETE` returns `204` with no body).

```js
// src/api.js — the only file that talks to the backend
import { BACKEND_BASEPATH } from './constants';

// Small helper: fetch + error check + JSON parse.
// Returns null for 204 (no body) instead of crashing on .json().
async function request(path, options = {}) {
  const response = await fetch(BACKEND_BASEPATH + path, options);
  if (!response.ok) {
    throw new Error(`${response.status} ${response.statusText}`);
  }
  if (response.status === 204) {
    return null;
  }
  return response.json();
}

const jsonHeaders = { 'Content-Type': 'application/json' };

// ---------- Vendors ----------
export function getVendors(query) {
  const qs = new URLSearchParams(query).toString();
  return request(`/vendors?${qs}`);
}

export function createVendor(data) {
  return request('/vendors', {
    method: 'POST',
    headers: jsonHeaders,
    body: JSON.stringify(data),
  });
}

export function updateVendor(id, data) {
  return request(`/vendors/${id}`, {
    method: 'PUT',
    headers: jsonHeaders,
    body: JSON.stringify(data),
  });
}

export function deleteVendor(id) {
  return request(`/vendors/${id}`, { method: 'DELETE' });
}

export function getVendorBottles(id) {
  return request(`/vendors/${id}/bottles`);
}

// ---------- Water Bottles ----------
export function getBottles(query) {
  const qs = new URLSearchParams(query).toString();
  return request(`/water-bottles?${qs}`);
}

export function createBottle(data) {
  return request('/water-bottles', {
    method: 'POST',
    headers: jsonHeaders,
    body: JSON.stringify(data),
  });
}

export function updateBottle(id, data) {
  return request(`/water-bottles/${id}`, {
    method: 'PUT',
    headers: jsonHeaders,
    body: JSON.stringify(data),
  });
}

export function deleteBottle(id) {
  return request(`/water-bottles/${id}`, { method: 'DELETE' });
}

// ---------- Boxes ----------
export function getBoxes(query) {
  const qs = new URLSearchParams(query).toString();
  return request(`/boxes?${qs}`);
}

export function createBox(data) {
  return request('/boxes', {
    method: 'POST',
    headers: jsonHeaders,
    body: JSON.stringify(data),
  });
}

export function updateBox(id, data) {
  return request(`/boxes/${id}`, {
    method: 'PUT',
    headers: jsonHeaders,
    body: JSON.stringify(data),
  });
}

export function deleteBox(id) {
  return request(`/boxes/${id}`, { method: 'DELETE' });
}
```

Notice: `getVendorBottles` uses the **documented** endpoint (`/vendors/{id}/bottles`) — the one from the refactor guide, not the `/vendors/bottle/{id}` shortcut. `VendorDetail.jsx` should call this function too.

**Test this step:** the pages still work exactly as before — you haven't used `api.js` anywhere yet. (You'll wire it up in Step 2.)

---

## Step 2 — `hooks/useVendors.js`: move the data logic out of the page

**Why:** the page currently mixes "how do I get and change data" with "what do I draw". A **custom hook** is just a function that uses React state. It lets you move all the list state, the fetch, the sorting, and the CRUD actions out of the component. The component keeps only the rendering.

Create `src/hooks/useVendors.js`:

```js
// src/hooks/useVendors.js
import { useEffect, useState } from 'react';
import { createVendor, deleteVendor, getVendors, updateVendor } from '../api';

const PAGE_SIZE = 5;

export default function useVendors() {
  const [vendors, setVendors] = useState([]);
  const [totalPages, setTotalPages] = useState(1);
  const [page, setPage] = useState(1);
  const [sortBy, setSortBy] = useState('id');
  const [order, setOrder] = useState('ASC');
  const [filters, setFilters] = useState({ filterBy: '', filterVal: '' });
  const [loading, setLoading] = useState(false);

  // Load the current page from the backend.
  async function refresh() {
    setLoading(true);
    try {
      const result = await getVendors({
        page: page - 1,
        pageSize: PAGE_SIZE,
        sortBy,
        sortMode: order,
        filterBy: filters.filterBy,
        filterVal: filters.filterVal,
      });
      setVendors(result.data);
      setTotalPages(Math.ceil(result.total / PAGE_SIZE));
    } finally {
      setLoading(false);
    }
  }

  // Reload whenever the page, sort, or filter changes.
  useEffect(() => {
    refresh();
    // oxlint may warn that refresh() is missing from this list — that's
    // expected. We want refresh to run when these *values* change.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, sortBy, order, filters]);

  function toggleSort(column) {
    if (sortBy === column) {
      setOrder(order === 'ASC' ? 'DESC' : 'ASC');
    } else {
      setSortBy(column);
      setOrder('ASC');
    }
    setPage(1);
  }

  // Create, update, delete: talk to the backend, then reload the list.
  // The server is the source of truth — never guess the list shape by hand.
  async function create(values) {
    await createVendor(values);
    await refresh();
  }

  async function update(id, values) {
    await updateVendor(id, values);
    await refresh();
  }

  async function remove(id) {
    await deleteVendor(id);
    await refresh();
  }

  function clearFilter() {
    setFilters({ filterBy: '', filterVal: '' });
    setPage(1);
  }

  return {
    vendors,
    totalPages,
    page,
    setPage,
    loading,
    toggleSort,
    filters,
    setFilters,
    clearFilter,
    create,
    update,
    remove,
  };
}
```

This one hook replaced: your list state, page state, totalPages, sortBy, order, the `refetchFlag` hack, `fetchVendors`, `sorting`, `clearFilter`, and the create/update/delete handlers. **One job per function, and every action ends with `refresh()` so the screen always matches the server.**

---

## Step 3 — `components/VendorForm.jsx`: one form, used for both create and edit

**Why:** your create modal and your update modal are the **same form** — same four inputs, same validation, only the initial values and the title differ. You currently have the same `useForm` block twice in the same file. Extract it once.

```jsx
// src/components/VendorForm.jsx
import { Button, Group, TextInput } from '@mantine/core';
import { hasLength, useForm } from '@mantine/form';
import Modal from './Modal';

export default function VendorForm({ title, initialValues, closeModal, onSubmit }) {
  const form = useForm({
    mode: 'uncontrolled',
    initialValues: {
      name: initialValues?.name ?? '',
      // String(...) matters: the API sends registrationNumber as a number,
      // and hasLength checks .length, which numbers don't have.
      registrationNumber: String(initialValues?.registrationNumber ?? ''),
      contractSignedDate: initialValues?.contractSignedDate ?? '',
      contractEndDate: initialValues?.contractEndDate ?? '',
    },
    validate: {
      name: hasLength({ min: 2, max: 10 }, 'Name must be 2-10 characters long'),
      registrationNumber: hasLength({ min: 2, max: 10 }, 'Registration Number must be 2-10 characters long'),
      contractSignedDate: hasLength({ min: 2, max: 100 }, 'Contract Signed Date must be 2-10 characters long'),
      contractEndDate: hasLength({ min: 2, max: 100 }, 'Contract End Date must be 2-10 characters long'),
    },
  });

  return (
    <Modal closeModal={closeModal} title={title}>
      <form
        onSubmit={form.onSubmit((values) => {
          onSubmit(values);
          form.reset();
        })}
      >
        <TextInput
          label="Name"
          withAsterisk
          key={form.key('name')}
          {...form.getInputProps('name')}
        />
        <TextInput
          label="Registration Number"
          withAsterisk
          mt="md"
          key={form.key('registrationNumber')}
          {...form.getInputProps('registrationNumber')}
        />
        <TextInput
          label="Contract Signed Date"
          withAsterisk
          mt="md"
          key={form.key('contractSignedDate')}
          {...form.getInputProps('contractSignedDate')}
        />
        <TextInput
          label="Contract End Date"
          withAsterisk
          mt="md"
          key={form.key('contractEndDate')}
          {...form.getInputProps('contractEndDate')}
        />
        <Group justify="flex-end" mt="md">
          <Button type="submit">{title}</Button>
        </Group>
      </form>
    </Modal>
  );
}
```

Bonus: the `String(...)` line fixes the update-form bug from the refactor guide (the "must retype the Registration Number" wall) — the form now always holds strings.

---

## Step 4 — the new `VendorList.jsx`: only "what to draw"

Now the page has one job: **render the state the hook gives it**. Notice it no longer imports `useEffect`, `useState` for data, `useForm`, or `fetch` at all.

```jsx
// src/pages/VendorList.jsx
import { useState } from 'react';
import { Button, Group, Loader, Pagination, Paper, Table, TextInput } from '@mantine/core';
import { Link } from 'react-router-dom';
import dayjs from 'dayjs';
import VendorForm from '../components/VendorForm';
import useVendors from '../hooks/useVendors';

export default function VendorList() {
  const {
    vendors, totalPages, page, setPage, loading,
    toggleSort, filters, setFilters, clearFilter,
    create, update, remove,
  } = useVendors();

  // null = no form open, 'create' = create mode, a vendor object = edit mode
  const [formState, setFormState] = useState(null);
  const [filterOpen, setFilterOpen] = useState(false);

  const rows = vendors.map((vendor) => (
    <Table.Tr key={vendor.id}>
      <Table.Td>
        <Button onClick={() => setFormState(vendor)}>Update</Button>
      </Table.Td>
      <Table.Td><Link to={`/vendor/bottle/${vendor.id}`}>{vendor.id}</Link></Table.Td>
      <Table.Td>{vendor.name}</Table.Td>
      <Table.Td>{vendor.registrationNumber}</Table.Td>
      <Table.Td>{dayjs(vendor.contractSignedDate).format('YYYY-MM-DD')}</Table.Td>
      <Table.Td>{dayjs(vendor.contractEndDate).format('YYYY-MM-DD')}</Table.Td>
      <Table.Td>
        <Button onClick={() => remove(vendor.id)}>Delete</Button>
      </Table.Td>
    </Table.Tr>
  ));

  return (
    <>
      <Group>
        <Button onClick={() => setFormState('create')}>Create Vendor</Button>
        <Button onClick={() => setFilterOpen(!filterOpen)}>Filter</Button>
        <Button onClick={clearFilter}>Clear</Button>
      </Group>

      {/* The filter is two plain inputs — no fake modal needed. */}
      {filterOpen && (
        <Paper shadow="xs" radius="xl" p="xl">
          <Group>
            <TextInput
              label="filterBy"
              value={filters.filterBy}
              onChange={(e) => setFilters({ ...filters, filterBy: e.currentTarget.value })}
            />
            <TextInput
              label="filterVal"
              value={filters.filterVal}
              onChange={(e) => setFilters({ ...filters, filterVal: e.currentTarget.value })}
            />
          </Group>
        </Paper>
      )}

      {formState === 'create' && (
        <VendorForm
          title="Create Vendor"
          closeModal={() => setFormState(null)}
          onSubmit={create}
        />
      )}
      {formState && formState !== 'create' && (
        <VendorForm
          title="Update Vendor"
          initialValues={formState}
          closeModal={() => setFormState(null)}
          onSubmit={(values) => update(formState.id, values)}
        />
      )}

      {loading ? (
        <Loader mt="xl" />
      ) : (
        <Table>
          <Table.Thead>
            <Table.Tr>
              <Table.Th>update</Table.Th>
              <Table.Th onClick={() => toggleSort('id')}>id</Table.Th>
              <Table.Th onClick={() => toggleSort('name')}>name</Table.Th>
              <Table.Th onClick={() => toggleSort('registrationNumber')}>registrationNumber</Table.Th>
              <Table.Th onClick={() => toggleSort('contractSignedDate')}>contractSignedDate</Table.Th>
              <Table.Th onClick={() => toggleSort('contractEndDate')}>contractEndDate</Table.Th>
              <Table.Th>delete</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>{rows}</Table.Tbody>
        </Table>
      )}

      <Pagination value={page} onChange={setPage} total={totalPages} color="gray" />
    </>
  );
}
```

Three modal booleans (`openModal`, `openModalTwo`, `updatedVendor`) became **one** variable. The filter "modal" (which was a `<Paper>` pretending to be a modal) is now two inputs. The `<Loader>` appears while loading — you had that on the to-do list from the earlier guide.

Move the file with `git mv src/VendorList.jsx src/pages/VendorList.jsx` and update the import in `App.jsx`:

```js
import VendorList from './pages/VendorList.jsx';
```

---

## Step 5 — do the same for Bottles and Boxes

Copy the pattern. The differences:

- **Bottles**: the form has a vendor dropdown. Put the `Select` inside `BottleForm.jsx` and pass the vendor list in as a prop:

```jsx
// src/components/BottleForm.jsx — the vendor part
<Select
  label="Vendor"
  data={vendors.map((vendor) => ({ value: String(vendor.id), label: vendor.name }))}
  key={form.key('vendorId')}
  {...form.getInputProps('vendorId')}
/>
```

  The page loads the vendor list once (for the dropdown) — `useVendors` can't help there, so fetch vendors with a plain effect in the page, or add a tiny `useVendorsForSelect()` hook that only returns the list. Your call — keep it as simple as it needs to be.

- **Boxes**: no vendor dropdown at all (remember `BoxDto` — length, width, height only). Boxes don't need the filter part of the pattern if you don't want it. Use `NumberInput` from Mantine for length/width/height instead of `TextInput` — it's the right tool for numbers.

- **Bottles search**: keep the `filterBy`/`filterVal` inputs, and remember the allowed field names from `WaterBottleResource.java`.

The shared pieces — `request()`, the hook shape, the form shape — are already written in `api.js`; for bottles and boxes you're writing four-line wrappers and a hook that mirrors `useVendors`.

---

## Rules of thumb (the whole guide in six lines)

1. **One file, one job.** A file that fetches, holds state, and draws the screen does three jobs.
2. **If you're copy-pasting, extract.** Three copies of a block means three places to fix a bug.
3. **Data logic lives in hooks, fetches live in `api.js`, forms live in components, pages only draw.**
4. **After every create/update/delete, reload from the server.** Never hand-edit the list with the response — that's how the `result.data` crashes happened.
5. **One modal, one state.** A single `formState` variable is easier to reason about than three booleans.
6. **No speculative abstractions.** Don't build a generic "CrudPage" that does everything — when three files genuinely repeat, extract the repeated part and stop there.

## When you're done

- [ ] `src/api.js` exists and every page imports its fetches from there — no `fetch(` left in page files
- [ ] `hooks/useVendors.js` (and bottles/boxes) hold all data state — no `useState` for lists/pages/sorting in the page files
- [ ] `components/VendorForm.jsx` (and bottles/boxes) hold the forms — one form per resource, used for both create and edit
- [ ] Page files are under ~100 lines and contain no `fetch`, no `useEffect`, no `useForm`
- [ ] Create, edit, delete, filter, sort, paginate all still work on all three pages
- [ ] The update form saves without retyping the Registration Number

You'll meet tools later that automate some of this for you — but because you wrote it by hand first, you'll know exactly what they do under the hood.
