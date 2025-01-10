# Ex2 Spreadsheet Application

Welcome to the **Ex2 Spreadsheet Application**! This project is a robust Java-based tool that mimics the functionality of traditional spreadsheet software while incorporating advanced features like formula evaluation, circular reference detection, and cell dependency management. Perfect for learning and real-world spreadsheet operations.

---

## 🌟 Key Features

- **Diverse Cell Types**: Supports text, numbers, and formulas.
- **Dynamic Formula Evaluation**: Automatically computes formulas, including references to other cells.
- **Circular Dependency Alerts**: Detects and prevents cyclic references.
- **Auto-Updating System**: Propagates changes dynamically to dependent cells.
- **Save & Load Functionality**: Export and import spreadsheet states seamlessly.
- **Dependency Analysis**: Computes depth and dependency chains for cells.

---

## 🚀 How to Use

### Option 1: Run via JAR File
1. Clone the repository:
   ```bash
https://github.com/Amitaibou/Ex2.git   ```
2. Execute the JAR file:
   ```bash
   java -jar Ex2Spreadsheet.jar
   ```

### Option 2: Run via IntelliJ IDEA
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/Ex2Spreadsheet
   ```
2. Open the project in IntelliJ IDEA.
3. Run the `Main` class under the `Ex2.Main` package.

---

## 📘 Example Workflow

### Interactive Commands:
1. **Set a Value:**
   ```bash
   set A1 15
   ```

2. **Add a Formula:**
   ```bash
   set B1 =A1*2
   ```

3. **Evaluate Formula:**
   ```bash
   value B1
   ```
   **Output:** `30`

4. **Save the Spreadsheet:**
   ```bash
   save mySpreadsheet.txt
   ```

5. **Load a Spreadsheet:**
   ```bash
   load mySpreadsheet.txt
   ```

---

## 🛠 Core Functionalities

### **Methods in `Ex2Sheet` Class:**

1. **`set(int x, int y, String value)`**:
    - Updates the value of the cell at position `(x, y)`.

2. **`get(int x, int y)`**:
    - Retrieves the `Cell` object from specified coordinates.

3. **`eval(int x, int y)`**:
    - Computes the value of a cell, handling formulas and dependencies.

4. **`save(String fileName)`**:
    - Exports the current state of the spreadsheet to a file.

5. **`load(String fileName)`**:
    - Loads a previously saved spreadsheet state from a file.

6. **`depth()`**:
    - Returns a matrix representing the dependency depth of each cell.

---

## 📂 Directory Structure

```
Ex2Spreadsheet
├── src
│   ├── Ex2Sheet.java       # Core spreadsheet logic
│   ├── Cell.java           # Cell interface
│   ├── SCell.java          # Concrete implementation of cells
│   ├── Utils.java          # Utility functions
│   └── Ex2Utils.java       # Constants and helper methods
├── test
│   └── Ex2SheetTest.java   # Unit tests for spreadsheet logic
└── README.md               # Project documentation
```

---

## ⚙️ Tools and Technologies

- **Java**: Primary programming language.
- **IntelliJ IDEA**: Development environment.

---

## 👤 Author

Developed with care and dedication by:

**[Amitai Bouzaglo ]**  
🌐 [GitHub Profile](https://github.com/your-username)

# Ex2