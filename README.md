# Beadandó feladatok osztott rendszerek tárgyból

A feladat egy egyszerű szerver-kliens alkalmazás létrehozása volt amely szóláncot alakít ki.

Tulajdonképpen az első beadandóval kezdődik, majd a másodikkal kiegészül a következő követelmény implementálása, mindez RMI és socketek segítségével.
A szerver adja a kezdő szót egy beépített szótárból és az utolsó betűvel kezdődő szót kell a felhasználónak visszaadni. A szerver jegyzi a hibás bemenetet és a szerver által elhasznált szavakat. A szerver egyszerre több klienst is ki tud szolgálni. Valamelyik verzióban lehetőség van két kliensnek mérkőzni. Ha elfogy a gép szókincse vagy a másik fél kilép, a másik játékos nyer.