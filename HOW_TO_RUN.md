# WatsFit — Food Inventory & Recipe Manager

A beautiful, 100% offline Windows desktop application built with Compose for Desktop, SQLite (SQLDelight), and Koin DI. Named in honor of Michael Watson, it helps you manage your food pantry, matches Mediterranean & Thai recipes to what you currently have, logs weight and resting heart rate progress, and helps you tone muscles using no-equipment bodyweight workouts.

## Prerequisites

- **Java Development Kit (JDK)**: JDK 17 or higher (JDK 21 recommended).
- **Windows OS**: Formatted for standalone execution.

## Features

- **Pantry Inventory**: Categorized storage lists (Pantry, Refrigerator, Freezer, Spices) with tracking for quantities, units, and expiration dates.
- **Nut-Free Mediterranean & Thai Recipe Matcher**: Recommends from 183+ built-in, dental-friendly soft-texture recipes. Excludes hard foods like nuts.
- **Deduction & Cook Flow**: Cooking a recipe deducts ingredients automatically and auto-adds missing items to the grocery checklist.
- **Grocery Checklist**: Manage shopping lists manually, auto-populate from chosen recipes, copy to clipboard, or export to a print-ready text file.
- **No-Equipment Workout Program**: 30-day toning programs across 3 difficulties (65+ exercises, 90+ routines).
- **Progress Trackers**: Log weight and resting heart rate trends with visualizations.

## How to Run & Build

To run the application locally:
```bash
./gradlew.bat run
```

To run all unit tests:
```bash
./gradlew.bat test
```

To package the application as a standalone Windows MSI installer:
```bash
./gradlew.bat packageMsi
```
The packaged installer will be built under the `build/compose/binaries` directory.
