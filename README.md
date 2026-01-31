# 📱 Erasmus Expense Tracker - Mobile Application Final Project for WUST (Poland)

**Erasmus Expense Tracker** is a modern Android application designed to help students and individuals manage their personal finances while living abroad. The app allows users to record expenses and incomes, set category-based monthly budgets, and visualize financial trends through animated, interactive charts.

Built using **Kotlin**, **Jetpack Compose**, and **MVVM architecture**, the project focuses on reactive UI, clean architecture, and real-time data visualization.

---

## ✨ Features

- 💸 Add, edit, and categorize **expenses and incomes**
- 📊 Interactive **financial dashboard** with animated charts
- 📅 Monthly filtering and calendar-based date selection
- 🎯 **Category-based monthly budgets**
- 🚨 Smart alerts when nearing or exceeding budgets
- 🔎 Filtering by name, type (income/expense), and category
- 🧭 Smooth navigation using Jetpack Navigation
- 🧩 Fully reactive UI powered by Kotlin Flows

---

## 🏗 Architecture

The app follows **MVVM (Model–View–ViewModel)** with a clean separation of concerns and reactive data streams.

### Data Layer
- **SQLite database** using **Jetpack Room**
- Entities: `Expense`, `Category`, `Budget`
- DAO interfaces for all CRUD operations
- Kotlin **Flow** streams for real-time updates
- Repository pattern to abstract data sources

### Presentation Layer
- **Jetpack Compose** UI
- State managed via `ViewModel` + `collectAsState()`
- Lifecycle-aware reactive UI updates

### Navigation
- **Jetpack Navigation-Compose**
- Centralized route definitions using a sealed class
- Scaffold with Drawer, TopAppBar, and Floating Action Button
- Context-aware FAB actions depending on the current screen

---

## 📊 Dashboard & Data Visualization

The main dashboard provides a visual overview of the user's financial activity through animated charts powered by **MPAndroidChart** embedded in Compose using `AndroidView`.

### Charts Included

- **Pie Charts** – Expenses and incomes grouped by category  
  - Slice highlighting on selection  
  - Smooth entry animations  

- **Line Chart** – Net balance over the last 28 days  
  - Animated left-to-right drawing  

- **Stacked Bar Chart** – Monthly income vs expenses per year  
  - Year selector for historical comparison  

All charts update automatically when the database changes thanks to Room + Flow integration.

---

## 🎯 Budget Tracking System

Users can define **monthly budgets per category**. The app continuously monitors spending and provides feedback:

- ⚠️ Warning at **90%** of the budget  
- ❌ Alert when **100%** is exceeded  

Notifications are shown using **Snackbars**, triggered by reactive budget checks in the ViewModel.

---

## 📱 Screens

- **Dashboard** – Balance summary, recent transactions, and charts  
- **Expense List** – All transactions with filters and monthly navigation  
- **Expense Form** – Add or edit transactions with date picker  
- **Category List & Form** – Manage expense categories  
- **Budget List & Form** – Set and monitor monthly budgets  

---

## 🛠 Tech Stack

| Layer | Technology |
|------|------------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM |
| Database | Room (SQLite) |
| Reactive Streams | Kotlin Flow |
| Charts | MPAndroidChart |
| Navigation | Navigation-Compose |
| Concurrency | Coroutines |

---

## 🚀 Getting Started

1. Clone the repository  
   ```bash
   git clone https://github.com/your-username/erasmus-expense-tracker.git
   ```

2. Open the project in **Android Studio**

3. Let Gradle sync and run the app on an emulator or physical device

---

## 📌 Future Improvements

- Cloud sync & authentication  
- Export data to CSV/Excel  
- Multi-currency support  
- UI/UX refinements and dark mode polish  

---

## 👨‍💻 Author

**Víctor Sebastián Marticorena**  
Mobile Computing – Application Programming Project
