# 🔗 MonitorBlockchain - Sepolia Network Monitor

## Project Description
[cite_start]**MonitorBlockchain** is an advanced application built with **Java 21+**, designed to monitor, collect, and report data from the **Ethereum Sepolia** public testnet[cite: 7, 154]. [cite_start]The project was developed following professional software engineering practices, including a three-tier architecture and rigorous qualitative testing[cite: 5, 21].

## System Architecture
[cite_start]The application implements a three-tier model, ensuring modularity and a clear division of responsibilities[cite: 25, 183]:

* [cite_start]**Access Layer:** Responsible for low-level connection to the Sepolia network using the **Web3j** library and node providers such as Alchemy[cite: 22, 184].
* [cite_start]**Business Logic Layer:** Handles raw data transformation, filtering, statistics aggregation, and error handling, including rate limiting management[cite: 22, 184].
* [cite_start]**Reporting Layer:** Responsible for real-time data presentation in the console, generating final reports, and exposing data via a **REST API**[cite: 180, 184].

## Key Functionalities
* [cite_start]**Block Monitoring (MVP):** Fetching data for a minimum of **100 latest blocks**, including Block Number, Hash, and Transaction Count [cite: 13, 14, 159-162].
* [cite_start]**Transaction Analysis (MVP):** Detailed retrieval for selected blocks, capturing Transaction Hash, Sender/Receiver addresses, ETH value, and Gas usage [cite: 15, 18, 163-167].
* [cite_start]**Session Statistics:** Automatic calculation of average gas consumption and the total number of processed operations [cite: 172-174].
* [cite_start]**Persistence (Extension):** Permanent data collection in an **H2 relational database**, allowing for historical analysis after system restarts[cite: 181].
* [cite_start]**Dashboard API:** Exposure of **JSON endpoints** enabling data visualization in external tools, such as a Streamlit dashboard[cite: 180, 285].

## Tech Stack
* [cite_start]**Language:** Java 21+[cite: 7, 58].
* [cite_start]**Libraries:** **Web3j** (Blockchain interaction), **Spring Boot** (Framework & API)[cite: 7, 62].
* [cite_start]**Database:** **H2** (SQL)[cite: 181].
* [cite_start]**Testing:** **JUnit 5**[cite: 32, 62].
* [cite_start]**Quality Assurance:** **JaCoCo** for code coverage measurement[cite: 33, 62].

## Methodology and Team
[cite_start]The project was carried out by **Ekipa Einsteina**[cite: 152, 188]:
* [cite_start]**Team Members:** Adrian Bielenik (Product Owner), Dawid Zieliński (Scrum Master), Marcel Siennicki [cite: 191-194].
* [cite_start]**Simplified Scrum:** Utilizing GitHub Projects for task management and backlog tracking[cite: 7, 146, 209].
* [cite_start]**Git-Flow:** Development using `feature/` branches, mandatory **Pull Requests**, and **Code Reviews**[cite: 41, 44, 210].
* [cite_start]**Time Budget:** A total of **60 man-hours** (approximately 15 hours per person) dedicated to the full implementation [cite: 142-146].

## Installation and Execution
1.  [cite_start]Configure your **Alchemy API key** in the `application.properties` file[cite: 59, 158].
2.  Run the application using Maven: `./mvnw spring-boot:run`.
3.  [cite_start]Monitor the **console** for real-time data updates[cite: 178].
4.  [cite_start]Upon shutdown, review the **summary report** detailing the session's work[cite: 179].

---
*Project completed as part of group coursework - May 2026.*