# PAP2024Z-Z04

 Diana Pelin,
 Katsyaryna Anikevich,
 Alesia Filinkova,
 Sofiya Yedzeika,
-----------------------------------
Uruchomienie projektu
*Wymagania wstępne:
    Projekt wymaga zainstalowanego środowiska Java w wersji 17 lub nowszej.
*Aby uruchomić projekt, należy wykonać główną klasę aplikacji:
    RegistrationLoginSystemApplication. (Klasa znajduje się w katalogu src/main/java.)
*Dostęp do aplikacji:
    Po uruchomieniu projektu aplikacja będzie dostępna pod adresem:
    http://localhost:8080/login
-----------------------------------
Etapy:
1.podział na zespoły, ustalenie tematu, dostarczenie dokumentu z wymaganiami
2.działający prototyp
3.działająca aplikacja
4.działająca aplikacja uwzględniająca uwagi prowadzącego z 3 etapu
------------------------------

Projekt zakłada stworzenie aplikacji webowej

Wymagania funkcjonalne:

1. Rejestracja i logowanie
* Rejestracja:
    * Użytkownik może zarejestrować konto, podając adres e-mail, nazwę użytkownika i hasło.
    * System sprawdza unikalność e-maila i nazwy użytkownika.
* Logowanie:
    * Użytkownik loguje się, podając e-mail i hasło.

2. Profil użytkownika
* Tworzenie profilu:
    * Użytkownik wprowadza dane osobiste: imię, nazwisko, kierunek studiów.
    * Możliwość dodania i edytowania planu zajęć z podziałem na dni tygodnia (godziny, nazwa przedmiotu, lokalizacja).
* Edycja profilu:
    * Użytkownik może aktualizować swoje dane i plan zajęć.

3. Menedżer zadań
* Dodawanie zadań:
    * Użytkownik wprowadza tytuł, opis, priorytet (niski, średni, wysoki) i termin realizacji.
* Priorytetyzacja i filtrowanie:
    * System umożliwia sortowanie zadań według priorytetu lub terminu.
* Edytowanie i usuwanie:
    * Możliwość edytowania i usuwania zadań.
* Oznaczanie jako wykonane:
    * Użytkownik może oznaczyć zadanie jako „zrealizowane”, co przenosi je do archiwum zadań.

4. Planowanie dnia
* Widok dzienny, tygodniowy, miesięczny:
    * Użytkownik przegląda zadania w różnych perspektywach czasowych.


5. Notatki
    * Możliwość dodawania krótkich notatek do każdego zadania.





Wymagania techniczne
1. Backend:
    * Język programowania: Java
    * Framework: Spring Boot (do obsługi logiki serwerowej, zarządzania użytkownikami, integracji z bazą danych )
2. Frontend:
    * Język programowania: JavaScript
    * Biblioteka: React
3. Baza danych:
    * System zarządzania bazą danych: Oracle Database (do przechowywania danych o użytkownikach, zadaniach i planach zajęć)
4. Inne narzędzia:
    * GitLab: Do wersjonowania kodu i zarządzania projektem.v