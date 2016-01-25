
### För att slippa ändra version på rehabstod-testtools och köra npm install vid varje ändring så behöver följande kommandon köras:

 ```sh
 cd rehabstodTestTools/
 npm link
 cd ..
 npm link rehabstod-testtools
```

### Då ändringar har gjorts i dessa moduler så bör man ändra versionsnummer för paketet innan incheckning