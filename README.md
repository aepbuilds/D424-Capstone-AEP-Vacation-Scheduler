[README.md](https://github.com/user-attachments/files/32029163/README.md)
# AEP Vacation Scheduler

## Title and Purpose

**AEP Vacation Scheduler** is an Android mobile application that allows a traveler to
track vacations and the excursions planned within each vacation. Users can create,
edit, and delete vacations and excursions, view detailed and list views of each, set
alerts that notify them when a vacation or excursion begins or ends, and share a
vacation's details through any installed sharing app (email, SMS, clipboard, etc.).
Vacation and excursion data is stored locally using the Room persistence library as
an abstraction layer over SQLite.

## Directions for Use

### Home Screen
1. Launch the app. The Home Screen displays a welcome message and a **Manage Vacations**
   button.
2. Tap **Manage Vacations** to open the Vacation List.

### Vacation List
1. The Vacation List displays every vacation currently saved, showing its title and
   date range.
2. Tap the **+** floating action button in the bottom-right corner to add a new
   vacation.
3. Tap any existing vacation in the list to open its detailed view.

### Adding / Editing a Vacation (Vacation Details screen)
1. Enter a **title**, **hotel/accommodation**, **start date**, and **end date**
   (format `MM/dd/yy`, e.g. `09/02/26`).
2. Tap **Save**.
   - If any field is left blank, a validation message appears and the vacation is
     not saved.
   - If a date is entered in an invalid format, a validation message appears.
   - If the end date is not after the start date, a validation message appears
     ("Vacation end date must be after the start date").
   - On success, the vacation is written to the Room database and an alert is
     scheduled for both the start date and the end date.
3. Tap **Delete** to remove the vacation.
   - If one or more excursions are still associated with the vacation, deletion is
     blocked and a validation message is shown.
   - Delete all associated excursions first, then delete the vacation.
4. Tap **Share** to open the Android share sheet with the vacation's title, hotel,
   and dates pre-populated as text, ready to send via email, SMS, clipboard, or any
   other installed sharing target.

### Excursions (within the Vacation Details screen)
1. Below the vacation fields, a list shows every excursion associated with that
   vacation.
2. Tap **Add Excursion** to open a blank Excursion Details screen, or tap an
   existing excursion in the list to edit it.
3. On the Excursion Details screen, enter an **excursion title** and **date**
   (format `MM/dd/yy`), then tap **Save**.
   - If a field is left blank or the date is formatted incorrectly, a validation
     message appears.
   - If the excursion date falls outside the parent vacation's start/end date
     range, a validation message appears ("Excursion date must be within the
     Vacation dates").
   - On success, an alert is scheduled for the excursion date.
4. Tap **Delete** on the Excursion Details screen to remove that excursion and
   return to the vacation's detail view.

### Alerts
- When a vacation is saved, two alerts are scheduled: one for the start date
  (announcing the vacation is starting) and one for the end date (announcing the
  vacation is ending), each displaying the vacation's title.
- When an excursion is saved, an alert is scheduled for its date, displaying the
  excursion's title.
- On first launch on a device running Android 13 (API 33) or higher, the app
  requests notification permission; this must be granted for alerts to appear.

### Mapping to Rubric Aspects
| Rubric Aspect | Where to Verify |
|---|---|
| B1 – Vacation CRUD, Room, delete validation | Vacation List → add/edit/delete a vacation; attempt to delete one with excursions |
| B2 – Vacation details fields | Vacation Details screen fields |
| B3a/b – Vacation detail view, enter/edit/delete | Vacation Details screen |
| B3c/d – Date format & end-after-start validation | Save a vacation with a bad date / end before start |
| B3e – Vacation alert | Save a vacation with a near-future start/end date and wait for the notification |
| B3f – Sharing | Tap Share on Vacation Details |
| B3g/h – Excursion list & CRUD | Excursions section within Vacation Details |
| B4 – Excursion details fields | Excursion Details screen fields |
| B5 – Excursion detail view, CRUD, validation, alert | Excursion Details screen; save with a bad date or an out-of-range date |

## Deployment

The signed APK included with this submission is deployed for **Android 8.0
(API 26) and higher**, and targets **Android 14 (API 34)**.

## Repository

GitLab repository:
https://gitlab.com/wgu-gitlab-environment/student-repos/aphomth/d308-mobile-application-development-android/-/tree/develop?ref_type=heads
