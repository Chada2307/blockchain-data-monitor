# 🔗 MonitorBlockchain - Sepolia Network Monitor

## Project Description
**MonitorBlockchain** is an advanced application built with **Java 21+**, designed to monitor, collect, and report data from the **Ethereum Sepolia** public testnet The project was developed following professional software engineering practices, including a three-tier architecture and rigorous qualitative testing.

## System Architecture
The application implements a three-tier model, ensuring modularity and a clear division of responsibilities:

* **Access Layer:** Responsible for low-level connection to the Sepolia network using the **Web3j** library and node providers such as Alchemy.
* **Business Logic Layer:** Handles raw data transformation, filtering, statistics aggregation, and error handling, including rate limiting management.
* **Reporting Layer:** Responsible for real-time data presentation in the console, generating final reports, and exposing data via a **REST API**.

## Key Functionalities
* **Block Monitoring (MVP):** Fetching data for a minimum of **100 latest blocks**, including Block Number, Hash, and Transaction Count.
* **Transaction Analysis (MVP):** Detailed retrieval for selected blocks, capturing Transaction Hash, Sender/Receiver addresses, ETH value, and Gas usage.
* **Session Statistics:** Automatic calculation of average gas consumption and the total number of processed operations .
* **Persistence (Extension):** Permanent data collection in an **H2 relational database**, allowing for historical analysis after system restarts.
* **Dashboard API:** Exposure of **JSON endpoints** enabling data visualization in external tools, such as a Streamlit dashboard.

## Tech Stack
* **Language:** Java 21+.
* **Libraries:** **Web3j** (Blockchain interaction), **Spring Boot** (Framework & API).
* **Database:** **H2** (SQL).
* **Testing:** **JUnit 5**.


## Methodology and Team
The project was carried out by **Ekipa Einsteina**:
* **Team Members:** Adrian Bielenik (Product Owner), Dawid Zieliński (Scrum Master), Marcel Siennicki .
* **Simplified Scrum:** Utilizing GitHub Projects for task management and backlog tracking.
* **Git-Flow:** Development using `feature/` branches, mandatory **Pull Requests**, and **Code Reviews**.
* **Time Budget:** A total of **30 man-hours** dedicated to the full implementation.

## Installation and Execution
1.  Configure your **Alchemy API key** in the `application.properties` file.
2.  Run the application using Maven: `./mvnw spring-boot:run`.
3.  Monitor the **console** for real-time data updates.
4.  Upon shutdown, review the **summary report** detailing the session's work.

---
*Project completed as part of group coursework - May 2026.*