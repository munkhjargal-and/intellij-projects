# Frontend Refactor Guide — wb-frontend

Your first version compiles and the pages render, but parts of it are broken and parts of the original guide were never finished. This guide walks you through finding and fixing both.

Same rules as the first guide: **read only the linked docs, no googling, no AI.** The Java files in the backend (`intellij-projects/Test -1/getting-started/src/main/java/mn/water/`) are your primary source of truth — when in doubt about what the API expects or returns, read the Java.

**Work one item at a time. For every item: reproduce the problem, find the cause, fix it, and verify the fix in the browser.** Write down the cause before you touch any code — that's the point of this exercise.

---

## Step 0 — Set up and test everything

1. Start the backend: open a terminal in `intellij-projects/Test -1/getting-started` and run `./mvnw quarkus:dev` (needs PostgreSQL running — see `application.properties`). The API lives at `http://localhost:8080`.
2. Start the frontend: open a terminal in `wb-frontend/my-app` and run `npm run dev`. The app lives at `http://localhost:5173`.
3. Open the browser DevTools (F12) and keep the **Console** and **Network** tabs open.
4. Go through **every flow on all three pages**: create, edit, delete, filter, sort, paginate. Click "View Bottles" on a vendor. Note down everything that breaks or looks wrong.

> **Known issue — the backend may not start.** If `./mvnw quarkus:dev` fails with a Flyway error about `V8__ALTER_Vendorid.sql` and a missing `vendor_id` column, that's a pre-existing bug in the migrations, not something you caused. Read the files in `src/main/resources/db/migration/` in order (V1 → V8): the final migration expects a column that the earlier ones never created. You need a database whose tables match the entities in `src/main/java/mn/water/entity/` — check `WaterBottle.java` to see the missing column. Fix the migrations or set the schema up another way, your call. (A `docker-compose.yml` for PostgreSQL is in this folder — `intellij-projects/tmp/docker-compose.yml` — if you want one.) Don't spend long here — it's backend, not your assignment.

You should now have a list of broken things. The items below are the ones that should be on it.

---

## Part A — Find and fix the bugs

### A1. Creating a Vendor crashes the page
- Reproduce: click "Create Vendor", fill the form, save. The page breaks and the console shows a "Cannot read properties of undefined" error.
- Read `VendorService.java` and `VendorResource.java`: what exactly does `POST /vendors` return? Now read the `handleSubmit` function in `src/VendorList.jsx`: the code reads a property off the response. Does the response actually have that property?
- Hint if stuck: `GET /vendors` returns a page object with 4 fields. Single-item endpoints (`POST`, `PUT`) do **not** wrap their result — the response *is* the item.
- Doc: https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch

### A2. Editing a Vendor empties the table
- Same root cause as A1, different function. Read `handleUpdate` and check what it does with the response.
- You'll hit a second wall first: the update form is prefilled from the list, and Registration Number arrives as a **number**, not a string. Read Mantine's `hasLength` docs — what does it do with a value that has no `.length`? The form won't save until that field is retyped. Fix the prefill (or the validator) so saving works — then watch what `handleUpdate` does with the response.
- Docs: https://mantine.dev/form/validation/

### A3. Deleting does nothing
- Reproduce: click Delete on a row. Nothing visibly happens and the row stays.
- Open the Network tab: what status code does `DELETE /vendors/{id}` return? What is the response **body**?
- Read the `.then()` chain in `handleDelete` — it tries to parse the body as JSON. What happens when you call `.json()` on an empty body?
- After a successful delete the list must be refreshed. The cleanest approach: call the list-fetch again.
- Docs: https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch · https://developer.mozilla.org/en-US/docs/Web/HTTP/Status

### A4. Creating a Box never reaches the backend
- Reproduce: click "Create Box", save, and watch the Network tab. Look closely at the request **URL**.
- Compare the `fetch` call in `src/BoxList.jsx` with the one in `src/BottleList.jsx`. One of them writes a variable into the URL correctly, the other doesn't.
- Doc: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Template_literals

### A5. Form validation never shows errors
- Reproduce: in the Box create form, type `0` in Length. No error appears, even though the validation says it should.
- Read the validation functions in `src/BoxList.jsx` (and the capacity validation in `src/BottleList.jsx`). They are arrow functions with curly braces — do they actually **return** the error message?
- Docs: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Functions/Arrow_functions · https://mantine.dev/form/validation/

### A6. The Box create/edit form has fields that don't belong
- Read `BoxDto.java`. A Box has `id`, `length`, `width`, `height` — nothing else.
- Yet the create modal shows a vendor dropdown and the update modal shows a `volume` input. Remove everything that isn't in `BoxDto`.
- Also: the vendor dropdown is bound to a form key that doesn't exist in the form's initial values, so it can never hold a value. Make sure every input is bound to a key that exists in the form.

### A7. Vendors' bottles use the wrong endpoint
- Read `VendorResource.java`. There are two endpoints that return a vendor's bottles. The guide documented `GET /vendors/{id}/bottles` — check the path used in `src/VendorDetail.jsx`.
- Fix it to use the documented path, and remove the leftover `console.log` calls in that file.

### A8. Contract End Date silently goes missing
- Reproduce: create a Vendor. After it appears in the table, the Contract End Date is empty. Edit any vendor and save — the end date becomes empty.
- Read `VendorDto.java` and `Vendor.java`. The list endpoint returns **entities**; `POST`/`PUT` take the **DTO**. The end-date field has a different name in the DTO than in the entity. Your create/update forms send the wrong name, so the backend ignores it.
- Fix: use the DTO's field name in your create/update forms. (The table still reads the entity's field name from the list response — those two stay as they are.)

---

## Part B — Finish what the guide asked for

### B1. Delete confirmation + success notifications
The guide said: *"Delete: confirm with the user, then delete. Show a success notification."* None of this exists yet.
- You already installed `@mantine/notifications` and mounted `<Notifications />` in `App.jsx` — read the docs and actually use it.
- For the confirmation, use Mantine's `Modal` (you'll do B4 first, which teaches you Modal).
- Docs: https://mantine.dev/x/notifications/

### B2. Loading state
None of the pages have a loading state. While a fetch is in flight, show Mantine's `<Loader>` instead of the table (or on top of it).
- Docs: https://mantine.dev/core/loader/ · https://react.dev/learn/state-a-components-memory

### B3. DatePicker for Vendor dates
The guide asked for a date picker for the Vendor date fields; you used plain text inputs.
- Docs: https://mantine.dev/dates/date-picker/ (note: `DatePickerInput` on the same page fits a form better — your choice)
- The backend expects a `Date` for these fields (`VendorDto.java`). What type does the DatePicker give you? What does `JSON.stringify` do to it? Verify a created vendor's dates come back intact (see A8 while you're there).

### B4. Replace your custom modal with Mantine's `<Modal>`
The guide told you to use Mantine's `Modal` component. You built your own in `src/Components/Modal.jsx` instead. Swap all create/edit dialogs over to Mantine's `Modal` and delete the custom one.
- Docs: https://mantine.dev/core/modal/

### B5. NumberInput for numeric fields
Capacity, length, width, height are numbers — use Mantine's `NumberInput`, not `TextInput`.
- Docs: https://mantine.dev/core/number-input/

### B6. "View Bottles" action per Vendor row
The guide's Vendors table requires three actions per row: Edit, Delete, **View Bottles**. Right now the row's id is a link. Add an explicit "View Bottles" button that goes to the vendor's bottles page (the route already exists).
- Docs: https://mantine.dev/core/button/ · https://mantine.dev/core/table/

### B7. Search input on the Bottles page
The guide said: *"Add a search input above the table. When the user types, call the backend with filterBy and filterVal."* You built a filter modal instead. Replace it with a `TextInput` above the table that triggers the fetch as the user types, using the allowed `filterBy` values from `WaterBottleResource.java`.
- Docs: https://mantine.dev/core/text-input/ · https://react.dev/learn/reacting-to-input-with-state

---

## Part C — Cleanup

1. Run `npm run lint` and fix every warning (unused imports, unused expressions, effect dependencies).
2. Every `fetch` should use the `BACKEND_BASEPATH` constant from `src/constants.ts`. At least one call hardcodes `http://localhost:8080` instead — fix it.
3. Remove stray `console.log` calls.
4. `src/index.jsx` imports the CSS file twice — clean that up.

---

## When you're done

Run through Step 0's checklist again on all three pages:

- [ ] Create works (row appears, no crash)
- [ ] Edit works (row updates in place, no empty table)
- [ ] Delete asks for confirmation, deletes, refreshes the list, shows a notification
- [ ] Loading indicator shows while fetching
- [ ] Vendor dates picked with DatePicker, stored and displayed correctly
- [ ] Numeric fields use NumberInput
- [ ] Every Vendor row has Edit, Delete, and View Bottles
- [ ] Bottles page searches as you type
- [ ] Box form only has Length, Width, Height
- [ ] All dialogs use Mantine's Modal
- [ ] `npm run lint` passes with zero warnings
