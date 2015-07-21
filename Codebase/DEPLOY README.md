Help for deploying applications on Heroku (Developed by Cervello Inc. [http://www.mycervello.com])
====================================================================================================

To automatically deploy the application to Heroku is necessary to use a service for Continuous Integration. In this example, we will use the service [TravisCI](https://travis-ci.com/). 

To use this service at the root of the project should be defined configuration file .travis.yml about this content (more details can be found in the [documentation](http://docs.travis-ci.com/user/build-configuration/)):

```
language: java
env:
	HEROKU_LOGIN=login
	HEROKU_PASSWORD=password
    HEROKU_APP=app-name
install:
  - sudo wget -qO- https://toolbelt.heroku.com/install-ubuntu.sh | sh
  - sudo apt-get install expect
script:
  - chmod a+x ./herokuEnviroment.sh
  - chmod a+x ./herokuLogin.exp
  - ./herokuLogin.exp
  - ./herokuEnviroment.sh
deploy:
  provider: heroku
  api_key: "<heroku ssh-key>"
  app: ${HEROKU_APP}
  ```

In the above example defines a variable HEROKU_APP name of your application on Heroku. Next, set the necessary tools (Heroku CLI, expect). Next, set the permissions on the scripts that will continue to be executed.

herokuLogin.exp script needs to authenticate to Heroku, to execute commands to create applications that install add-ons, etc.

```
#!/usr/bin/expect
spawn heroku "login"

expect "Email:"

send ${HEROKU_LOGIN};

send "\r"

expect "Password (typing will be hidden):"

send ${HEROKU_PASSWORD}

send "\r"

interact
```

herokuEnviroment.sh script contains heroku command to create the application, install add-ons, etc.

```
#!/bin/sh
 
heroku apps:create ${HEROKU_APP}
```

Point deploy file .travis.yml required for automatic application deployment on heroku. It establishes the ssh key account where it will be installed, as well as a name to an application that is automatically installed from a variable defined above.

After downloading all the changes on github with the command git push TravisCI service automatically collects application will create all the necessary load on Heroku and assembled package on Heroku

Approximate order of steps to deploy applications on Heroku account of the client:

1. Create a separate branch in the repository to the client.
2. Change / add environment variables (for Heroku, Google API) for the client file .travis.yml
3. Run git push to create a branch.

Everything else will be executed automatically



