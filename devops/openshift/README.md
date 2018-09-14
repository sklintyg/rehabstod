
    ./oc create secret generic "rehabstod-test-env" --from-file=test/env/ --type=Opaque


### Create Build template

    ./oc process buildtemplate-webapp -p APP_NAME=rehabstod-test -p GIT_URL=https://github.com/sklintyg/rehabstod.git -p STAGE=test | ./oc apply -f -

### Create pipeline
    
    ./oc process pipelinetemplate-test-webapp -p APP_NAME=rehabstod-test -p STAGE=test -p SECRET=nosecret -p TESTS="-" | ./oc apply -f -
