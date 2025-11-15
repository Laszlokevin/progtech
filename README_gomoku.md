# Gomoku / Amőba (10x10) — Java 21 Maven project

Ez egy egyszerű parancssoros amőba (Gomoku) játék Java 21 és Maven alapokon.

Főbb jellemzők:
- 10x10 tábla (alapértelmezett), de a Board osztály támogatja a konfigurálható méreteket (5 <= M <= N <= 25)
- Humán játékos: 'x' (kezd)
- Gépi játékos: 'o' (random lépések választ)
- A kezdő jel az asztal egyik középső mezőjére kerül automatikusan
- Minden lépésnek legalább diagonálisan érintkeznie kell a már létező kövekkel
- Győzelem: 4 azonos jel egymás mellett (függőlegesen, vízszintesen vagy átlósan)
- Játék végeredményét mentjük egy beágyazott H2 adatbázisba; lehet lekérdezni a high-score listát
- Maven konfiguráció: JUnit5, Mockito, Logback, JaCoCo, Checkstyle, assembly és jar plugin

Futtatás:
1. Build:
   mvn clean install

2. Futtatás:
   java -jar target/gomoku-1.0-SNAPSHOT-jar-with-dependencies.jar

A játék parancssoros: megkérdezi a játékos nevét és végigvezeti a lépéseket. A lépés formátuma: például `e5` (oszlop betű, sor szám). Az oszlopok a..j, a sorok 1..10.

Adatbázis (H2) fájl alapértelmezés szerint: ./data/gomoku; ha szükséges, módosítható a ScoreDao-ban.
