[![Build Status](https://travis-ci.org/aleksey-suprun/meter-tracker.svg?branch=master)](https://travis-ci.org/aleksey-suprun/meter-tracker)

# Meter Tracker
The main goal of this project is to automate and simplify gathering indications from analog meter devices such as water meter,
electricity meter, gas meter. As an input data it uses digital photos of meter device, made by mobile phone camera or any other 
digital camera.

The Meter Tracker project aims to detect indication's region on device's photo and tries to recognize symbols in it. User has 
ability to validate and if it is necessary to correct recognition result and mark any symbol from result as training data for
improvement future recognition results.

# Google Drive as a file storage
Since the Google OAuth 2.0 system supports server-to-server interactions such as those between a web application and a Google service. For this scenario there is a need in service account, which is an account that belongs to application instead of to an individual end user. Application calls Google APIs on behalf of the service account, so users aren't directly involved. 

1. Create new project in your Google Developers Console (https://console.developers.google.com/projectcreate)
2. Type name of project and hit `Create` button
3. Select created project
4. Go to `IAM & admin`/`Service accounts` section and create new service account (only name is required)
5. Go to `APIs & Services` section
    * Add Google Drive API to application under `Dashboard` subsection
    * Create service account key for service account under `Credentials` subsection
    * Save generated JSON file on your computer
6. Go to your Google Drive page (https://drive.google.com) and create new folder
7. Change sharing settings:
    * Allow read-only access to folder by link
    * Share folder with your service account via email (available on `IAM & admin`/`Service accounts` page)
8. Configure following application properties:
    * `application.fs.gdrive.directory` - created Google Drive directory name
    * `application.fs.gdrive.serviceAccountKey` - path to downloaded JSON key
