# Frontend Guide — React 19 SPA + Mantine + Fetch

## What You're Building

A single-page application (SPA) that talks to the Quarkus backend at `http://localhost:8080`.

The backend has **three resources** (read the Java files to understand the shapes):

| Resource       | API base path         | Fields |
|----------------|-----------------------|--------|
| **Vendors**    | `/vendors`           | `id`, `name`, `registrationNumber`, `contractSignedDate`, `contractEndDate` |
| **WaterBottles** | `/water-bottles`   | `id`, `vendorId`, `brand`, `capacity`, `barcode` |
| **Boxes**      | `/boxes`             | `id`, `length`, `width`, `height` |

Your job: build pages that **list**, **create**, **edit**, and **delete** each resource.

---

## API Contracts (Read These)

Every list endpoint (`GET /vendors`, `GET /water-bottles`, `GET /boxes`) accepts these **query parameters**. Pay attention to defaults and valid field names — read the Java `@Pattern` annotations in the resource files to know what strings are allowed for `sortBy` and `filterBy` in each resource.

| Parameter    | Type    | Default            | Notes                                  |
|-------------|---------|--------------------|----------------------------------------|
| `page`      | int     | `0`                | starts at 0                            |
| `pageSize`  | int     | `100`              | how many items per page                |
| `sortBy`    | string  | varies by resource | field name to sort by                  |
| `sortMode`  | string  | `ASC`              | `ASC` or `DESC`                        |
| `filterBy`  | string  | (null)             | field name to filter on                |
| `filterVal` | string  | (null)             | value to filter by                     |

Every list endpoint returns a JSON object that has these 4 fields: `page` (int), `pageSize` (int), `total` (int), and `data` (array of items). Check the Java class `SomeDto` for the exact field names.

Single-item endpoints (`GET /{id}`, `POST /`, `PUT /{id}`) return one item as a JSON object. The `DELETE /{id}` endpoint returns nothing on success.

### Special endpoints

- `GET /vendors/{id}/bottles` — returns a list of WaterBottle objects belonging to that vendor
- `GET /boxes/{id}/volume` — returns a single float

### Request body shape for POST and PUT

Read `WaterBottleDto.java`, `VendorDto.java`, and `BoxDto.java` in the `dto` package. Send a JSON object with the same field names. Do not include fields the DTO doesn't have.

Content-Type header must be `application/json` for POST and PUT requests.

---

## CORS — needs a fix in the backend

Your browser won't let the React app (running on port 5173) talk to the Java app (port 8080) unless the Java app sends the right CORS headers.

Open `src/main/resources/application.properties`. The Quarkus property for enabling CORS is `quarkus.http.cors`. Set it to `true`. That's it — Quarkus will allow all origins in dev mode. Save the file and restart the backend.

---

## Project Setup

Do **not** use create-react-app — it's deprecated. Use **Vite**.

Read ONLY these pages (no googling, no AI):

1. **Scaffolding**: https://vite.dev/guide/#scaffolding-your-first-vite-project
2. **Adding Mantine**: https://mantine.dev/getting-started/#install-with-vite (pay attention to the PostCSS setup step — styles won't work without it)
3. **Running**: `npm run dev` — opens at `http://localhost:5173`

---

## How to Structure Your App (read these docs, in order)

### Step 1 — Start with one page

Read this page only: https://react.dev/learn/your-first-component

Create a `VendorList` component in `src/VendorList.jsx`. Call it from `App.jsx`. Make it show a hardcoded list of vendors (just JSX with static data, no fetch yet). This is just to prove the component renders.

### Step 2 — Fetch real data

Read these pages only:

- https://react.dev/learn/state-a-components-memory (useState)
- https://react.dev/learn/synchronizing-with-effects (useEffect)
- https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch (fetch)

Call `fetch("http://localhost:8080/vendors?page=0&pageSize=100")` inside `useEffect`. Put the result into state with `useState`. Use a second piece of state for loading. Render the vendors in a list.

### Step 3 — Make it look good with Mantine

Read these pages only (**start here**, not the whole Mantine docs):

- https://mantine.dev/core/table/ — put your data in a Table
- https://mantine.dev/core/button/ — buttons
- https://mantine.dev/core/modal/ — open a modal for create/edit
- https://mantine.dev/core/text-input/ — text input fields
- https://mantine.dev/core/number-input/ — number input fields
- https://mantine.dev/dates/date-picker/ — date picker for Vendor dates
- https://mantine.dev/core/notification/ — show success/error messages
- https://mantine.dev/core/loader/ — loading spinner

Replace your loading text with Mantine's `<Loader>` component. Replace the plain HTML list with a Mantine `<Table>` that has one column per field.

### Step 4 — Create + Edit

Read this page only (this is the pattern):

https://react.dev/learn/reacting-to-input-with-state

Make a button labeled "Create Vendor" that opens a Mantine `<Modal>`. Inside the modal, put text inputs and number inputs for each field of a Vendor. Use `useState` for each field. When the user clicks "Save", call `fetch` with `method: "POST"`.

For edit: open the same modal but pre-fill the inputs with the current vendor's values. When the user clicks "Save", call `fetch` with `method: "PUT"` and the vendor's `id` in the URL.

For delete: add a "Delete" button per row. When clicked, call `fetch` with `method: "DELETE"`. Refresh the list after.

---

## What to Build (3 Pages)

### Page 1 — Vendors (default page)

Table columns: Name, Registration Number, Contract Signed Date, Contract End Date, Actions (Edit, Delete, View Bottles)

"Create Vendor" button opens a modal with fields for all Vendor properties.

Edit: opens same modal but pre-filled with that vendor's values.

Delete: confirm with the user, then delete. Show a success notification.

"View Bottles": clicking this shows the Water Bottles page, but it should only show bottles belonging to that vendor. The API endpoint `GET /vendors/{id}/bottles` exists for this.

### Page 2 — Water Bottles

Table columns: Brand, Capacity, Barcode, Vendor (show the vendor name — you'll need to fetch vendors too or handle this), Actions.

Same CRUD pattern as vendors. The DTO for creating/updating includes `vendorId`.

Add a search input above the table. When the user types, call the backend with filterBy and filterVal query parameters. Open the Java file for WaterBottle and read its `@Pattern` annotation on `filterBy` to see which fields are allowed.

### Page 3 — Boxes

Table columns: Length, Width, Height, Volume, Actions.

Three of the four columns come from the list endpoint. For the Volume column, you can either calculate `length * width * height` yourself in JavaScript, or call `GET /boxes/{id}/volume` for each row. Your choice.

Same CRUD pattern.

---

## Routing between pages

Read only this: https://react.dev/learn/describing-the-ui#keeping-components-pure

You don't need React Router. Use a piece of state (string: `"vendors"`, `"bottles"`, or `"boxes"`) to track which page is visible. Render the right component based on that state.

Use Mantine's Tabs (https://mantine.dev/core/tabs/) for the navigation bar instead of plain buttons.

---

## If Anything Goes Wrong

- **"Failed to fetch" or CORS error**: Did you add the CORS property to `application.properties` and restart the Java backend?
- **Mantine looks broken / no styles**: Did you follow the PostCSS setup step from the Mantine vite guide?
- **"Cannot read properties of undefined"**: Your fetch response shape might not match what you expected. Open the browser's dev tools console and inspect what the API actually returns before accessing properties.
- **Backend error (500)**: Check the terminal where the Java app is running. The error stack trace will tell you what went wrong.
- **Button shows nothing / no error**: Open the browser's Developer Tools (F12) → Console tab. Any JavaScript error will appear there.

---

## Doc Link Reference (only these — no google, no AI)

All the documentation you need is in these links. Start from the top and work your way down. Do not deviate.

1. Vite scaffolding: https://vite.dev/guide/#scaffolding-your-first-vite-project
2. Mantine install with Vite: https://mantine.dev/getting-started/#install-with-vite
3. React: Your First Component: https://react.dev/learn/your-first-component
4. React: useState (state: a component's memory): https://react.dev/learn/state-a-components-memory
5. React: useEffect (synchronizing with effects): https://react.dev/learn/synchronizing-with-effects
6. MDN: Using the Fetch API: https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch
7. React: Reacting to Input with State: https://react.dev/learn/reacting-to-input-with-state
8. React: Keeping Components Pure (for the app structure idea): https://react.dev/learn/describing-the-ui#keeping-components-pure
9. Mantine Table: https://mantine.dev/core/table/
10. Mantine Button: https://mantine.dev/core/button/
11. Mantine Modal: https://mantine.dev/core/modal/
12. Mantine TextInput: https://mantine.dev/core/text-input/
13. Mantine NumberInput: https://mantine.dev/core/number-input/
14. Mantine DatePicker: https://mantine.dev/dates/date-picker/
15. Mantine Notification: https://mantine.dev/core/notification/
16. Mantine Loader: https://mantine.dev/core/loader/
17. Mantine Tabs (navigation): https://mantine.dev/core/tabs/

---

**One step at a time.** Page 1 first (Vendors). When it works, move to Water Bottles. Then Boxes.
