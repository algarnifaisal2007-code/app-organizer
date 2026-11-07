# app-organizer
تطبيق لتنظيم وإدارة التطبيقات - App Organizer Application

## Flipper Zero app

This repository holds a Flipper Zero external app (`.fap`), described by `application.fam`.

### Build in CI

`.github/workflows/build.yml` runs on every push and pull request, and can be started by hand with **Run workflow**.
It uses the [Build Flipper Application Package (.fap)](https://github.com/marketplace/actions/build-flipper-application-package-fap) action (`flipperdevices/flipperzero-ufbt-action`) to:

1. build the app against the **release** and **dev** firmware SDK channels,
2. upload the resulting `.fap` file as a workflow artifact (open the run's **Summary** page to download it),
3. lint the C sources with `ufbt lint`.

### Build locally

```sh
pip install --upgrade ufbt
ufbt            # build; output goes to dist/
ufbt launch     # build, install and run on a connected Flipper
ufbt format     # auto-format sources
```
