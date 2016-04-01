# Load-testing with Gotling

### What is Gotling?
Gotling is a simplistic load-testing framework for HTTP and TCP services. See https://github.com/eriklupander/gotling

### Installation

To execute a Gotling load test, one either needs to build Gotling from source or you need to download a binary release for your operating system.

#### Using a binary release (recommended)

1. Download the latest binary release of Gotling from github:

https://github.com/eriklupander/gotling/releases/

Per 2016-04-01, v0.2-alpha exists for Mac OS X 10.11.

2. Place the downloaded binary in the _/rehabstod/tools/loadtest/gotling_ project folder (it's gitignored)

#### Building from source

1. Install the Go SDK from https://golang.org/doc/install, follow all instructions closely regarding env. variables etc. and make sure you select the correct operating system.
2. Clone the gotling source code into a folder of your choice.
3. Build the executable from the root of the cloned project using: go build -o gotling src/github.com/eriklupander/gotling/*.go
4. Copy the gotling executable into _/rehabstod/tools/loadtest/gotling_

### Configuring and running the ListActiveSickLeavesForCareUnit test

#### Test definition ListActiveSickLeavesForCareUnit
The definition for the rehabstod ListActiveSickLeavesForCareUnit gotling test resides in /samples/rehabstod.yml

    ---
    iterations: 50
    users: 120
    rampup: 30
    feeder:
      type: csv
      filename: rehabstod.csv
    actions:
      - http:
          title: Submit query
          method: POST
          url: http://localhost:8080/inera-certificate/list-active-sick-leaves-for-care-unit/v1.0
          template: rehabstod/ListActiveSickLeavesForCareUnitRequest.xml
          accept: application/xml
      - sleep:
          duration: 30
          
Please modify iterations, users, rampup etc to your liking. The example above will yield about 4 requests per second.

#### Changing Care Units
Open /data/rehabstod.csv

    careUnitHsaId
    IFV1239877878-1042
    IFV1239877878-1045
    IFV1239877878-104D

The simulation will cycle through these care unit id's. Just add/remove as you see fit.

#### SOAP request template
The SOAP request is read from the file /templates/rehabstod/ListActiveSickLeavesForCareUnitRequest.xml

    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:riv:itintegration:registry:1" xmlns:urn1="urn:riv:clinicalprocess:healthcond:rehabilitation:ListActiveSickLeavesForCareUnitResponder:1" xmlns:urn2="urn:riv:clinicalprocess:healthcond:certificate:types:2">
      <soapenv:Header>
        <urn:LogicalAddress>spelar-ingen-roll</urn:LogicalAddress>
      </soapenv:Header>
      <soapenv:Body>
        <urn1:ListActiveSickLeavesForCareUnit>
          <urn1:enhets-id>
            <urn2:root>1.2.752.129.2.1.4.1</urn2:root>
            <urn2:extension>${careUnitHsaId}</urn2:extension>
          </urn1:enhets-id>
          <!--You may enter ANY elements at this point-->
        </urn1:ListActiveSickLeavesForCareUnit>
      </soapenv:Body>
    </soapenv:Envelope>

Note the ${careUnitHsaId} parameter that Gotling will replace with the values from /data/rehabstod.csv

#### Running
Prereq: You should be in the root /rehabstod/tools/loadtest/gotling folder, i.e. where you have the /data, /samples and /templates folders as direct subfolders.

    ./gotling samples/rehabstod.yml
    
The load test should start within a second.

    ./gotling samples/rehabstod.yml 
    Starting WebSocket server
    CSV feeder fed with 3 lines of data
    Time: 1 Avg latency: 151029 μs req/s: 3
    Time: 2 Avg latency: 160622 μs req/s: 5
    Time: 3 Avg latency: 161849 μs req/s: 3
    Time: 4 Avg latency: 158906 μs req/s: 4
    Time: 5 Avg latency: 160573 μs req/s: 4
    Time: 6 Avg latency: 155654 μs req/s: 4
    ...
    
That's it!

At any time, open http://localhost:8182 to load the live dashboard. After the test has finished, see /logs folder for some report data.


### Test definition for Rehabstöd REST services
There is also a test that uses rehabstod-web REST services in a manner similar to a real web browser, e.g. performing login, loading care unit stats, loading sjukfall and finally logging out. The test resides in /samples/rehabstod-web.yml

Note that the test requires fake login to be active. It can run against test/demo environments using self-signed certificates for https.  

#### Running
Prereq: You should be in the root /rehabstod/tools/loadtest/gotling folder, i.e. where you have the /data, /samples and /templates folders as direct subfolders.

    ./gotling samples/rehabstod-web.yml
    
By default, this test runs 50 concurrent users 30 iterations, averaging ~7 req/s.