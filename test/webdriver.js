/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
const { exec } = require('child_process');

let command = 'google-chrome --version';

if (process.platform === "win32") {
  command = 'wmic datafile where name="C:\\\\Program Files (x86)\\\\Google\\\\Chrome\\\\Application\\\\chrome.exe" get Version /value\n';
} else if (process.platform === "darwin") {
  command = '/Applications/Google\\ Chrome.app/Contents/MacOS/Google\\ Chrome --version';
}

exec(command, (err, stdout, stderr) => {
  var regex = /[^0-9.]/gi;

  var version = stdout.replace(regex, '');
  console.log(`installed chrome: ${version}`);

  exec('node node_modules/webdriver-manager/bin/webdriver-manager update --versions.chrome  ' + version + ' --gecko false --standalone false', (err, stdout, stderr) => {
    console.log(`${stdout}`);
  })
});
