# Frontend Refactor Guide — Part 2 (Follow-up)

You fixed part of Part 1. I ran the app and tested every flow with a real browser. Some Part 1 items are still unfinished, and the same bug you fixed on the Vendors page still exists on the other two pages. This guide lists exactly what's left.

Same rules as before: **read only the linked docs, no googling, no AI.** Read the Java when in doubt. Reproduce each problem first, write down the cause, then fix it, then verify in the browser.

---

## Part 1 — Finish what's left from Part 1

### 1. Deleting still doesn't refresh the list (all three pages)
- Reproduce: delete any vendor. Watch the Network tab: the `DELETE` succeeds (status 204), the row is gone from the database — but the row stays on screen until you refresh the page. The console shows a JSON parsing error.
- This is A3 from Part 1, still unfixed. Read the `.then()` chain in every `handleDelete` — what does a 204 response with an **empty body** do to `.json()`? What does the code do *after* that call?
- The fix: after a successful delete, re-fetch the current page. Note this applies to `VendorList`, `BottleList`, and `BoxList`.
- Docs: https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch · https://developer.mozilla.org/en-US/docs/Web/HTTP/Status

### 2. The update form still won't save without retyping the Registration Number
- Reproduce: open a vendor's Update modal, change only the Name, click save. A validation error appears on Registration Number even though you didn't touch it.
- This is the A2 wrinkle from Part 1. The prefill fills the field from the list, and the value arrives as a **number**. Read Mantine's `hasLength` docs: what does it do with a value that has no `.length`?
- Fix the prefill so the field always holds a string — then the form saves on its own.
- Docs: https://mantine.dev/form/validation/

### 3. The update modal is closed with a hack
- Read `handleUpdate` in `src/VendorList.jsx`. After a successful save it calls `openUpdateModal(false)` to close the modal. That works by accident (a `false` value unmounts the modal) but it's the wrong tool — `openUpdateModal` is for *opening* a modal with a vendor's data. There is a state setter whose job is closing it.
- Clean this up so the intent is obvious.

### 4. Creating a Box still crashes the page
- Reproduce: create a box. Watch the Network tab: the request now goes to the right URL (your A4 fix worked). Then the page goes blank and the console shows an error in the `<BoxList>` component.
- This is the exact A1 bug, but in `src/BoxList.jsx`. Read its `handleSubmit` — what does it read off the response? Compare with the fixed version in `VendorList.jsx`.

### 5. The Box forms still have fields that don't belong
- Open the Create Box modal: there's still a vendor dropdown. Open the Update Box modal: there's still a vendor dropdown (labeled `boxId`) — even though you removed the `volume` input.
- Read `BoxDto.java` again. A Box has no vendor. Remove both dropdowns and the vendor-fetching code that only exists to feed them.

### 6. The bottles page still uses the wrong endpoint
- Open a vendor's bottles page and watch the Network tab: the fetch goes to `/vendors/bottle/{id}`. The documented endpoint is `GET /vendors/{id}/bottles` — read `VendorResource.java`.
- Change the fetch in `src/VendorDetail.jsx` to the documented path. (You changed the *route* to match the wrong endpoint last time — that's backwards. The route is your choice; the fetch must hit the documented API.)
- The route in `App.jsx` and the links in `VendorList.jsx` are up to you — just make them agree.

---

## Part 2 — Apply the same fixes to the other two pages

You fixed the create/edit crash on the Vendors page (A1/A2). The identical bugs are still in `BottleList.jsx` and `BoxList.jsx`:

### 7. Creating a Bottle crashes the page
- Reproduce: create a bottle (pick a vendor in the dropdown first). The `POST` succeeds, then the page goes blank.
- Same cause as item 4 and A1. Read `handleSubmit` in `src/BottleList.jsx`.

### 8. Editing a Bottle crashes the page
- Reproduce: edit any bottle, save. Watch the Network tab: the `PUT` succeeds (the database is updated), then the page goes blank.
- Read `handleUpdate` in `src/BottleList.jsx` — same A2 root cause.

### 9. Editing a Box crashes the page
- Same as item 8, in `src/BoxList.jsx`. The `PUT` succeeds, then the page goes blank.

The pattern you should now recognize: **`POST` and `PUT` return the item directly, not wrapped in `{data: ...}`.** Single-item endpoints answer with the item; only the list endpoints return `{page, pageSize, total, data}`.

---

## Part 3 — After the bugs are fixed, reorganize the code

Once every item above works, your bugs are fixed but the code is hard to live with: three 250–370 line page files, each doing five jobs at once, with most of the code copy-pasted between them. Fixing the same bug in three files is exactly how the mess above happened.

**Read `frontend-code-organization.md` in this folder and follow it from start to finish.** It splits your pages into small single-purpose files — `api.js` for fetches, hooks for data state, form components for the modals — with complete copy-paste-ready code, no new libraries. It also fixes a few of this guide's items as a side effect (the delete-refresh pattern, the Registration Number prefill). Do the bug fixes first, then the reorganization — fixing bugs is easier in code you already understand, and reorganizing is safer once the behavior is correct.

---

## When you're done

- [ ] Delete refreshes the list on all three pages, no console error
- [ ] Update modal saves without retyping the Registration Number
- [ ] Update modal closes cleanly (no hack)
- [ ] Create and edit work on all three pages — no blank screens, no console errors
- [ ] Box forms have only Length, Width, Height
- [ ] VendorDetail fetches `/vendors/{id}/bottles`
- [ ] Create a vendor → edit it → delete it, same for bottles and boxes, all without reloading
