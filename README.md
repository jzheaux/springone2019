Multi-tenancy with OAuth 2.0 and Spring Security
------------------

This repo consists of four [Spring Boot](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html) applications that are secured using [Spring Security](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/) and [OAuth 2.0](https://oauth.net/2/).

For the application to work, please take the following steps after cloning to your local machine:

1. Modify your /etc/hosts file

    The demo code points to the endpoint `idp:9999` for the Identity Provider.
    It also refers to each tenant by hostname. 
    So, your machine can resolve this hostname on its own by adding the following to `/etc/hosts`:

    ```bash
    127.0.0.1   idp
    127.0.0.1   one
    127.0.0.1   two
    127.0.0.1   three
    127.0.0.1   four
    ```

    You may need `sudo` privileges to edit the file.

2. Start the keycloak docker container

    These demos work with a pre-configured Authorization Server, which you can start up with the following command inside the /etc directory of the demo:

    ```bash
    docker-compose up -d
    ```

    Thereafter, you can stop this server with:

    ```bash
    docker-compose stop
    ```

    And start it with:

    ```bash
    docker-compose start
    ```

3. Configure `tenant`

    To do the more advanced demo of adding a tenant dynamically, the `tenant` application needs to be configured with the `master-realm` password in Keycloak.

    To do this 

        - Log into Keycloak at http://idp:9999 with admin/asdfasdfasdf
        - Select the `master` realm
        - Select `Clients`, and select `master-realm`
        - Change `Access Type` to `confidential`
        - Disable `Direct Access Grants Enabled`
        - Enable `Service Accounts Enabled`
        - Save the client
        - Go to the `Service Account Roles` tab
        - Add `admin` to the effective roles
        - Go to the `Credentials` tab
        - Copy the secret into the `tenant` `application.yml` file

4. Start the servers

    There are four servers, `message`, `user`, `inbox`, `tenant`.
    To start each one, you can simply run from the command line from each directory:

    ```bash
    ../mvnw spring-boot:run
    ```

5. Use the application:

    When you navigate to http://one:8080, you'll be invited to authenticate. You can do:

    * rob / password
    * joe / password
    * josh / password

    When you navigate to http://two:8080, you'll be invited to authenticate as well. You can do:

    * filip / password
    * ria / password
    * josh / password

6. Add a tenant

    To add a tenant, simple run from the `etc` directory in the project:

    ```bash
    ./add-tenant three
    ```

    Then, you'll be able to navigate to http://three:8080 and be invited to authenticate.
    You can do:

    * rob / password
    * joe / password
    * josh / password

Also, take a look at [the Java to HTTP mappings](https://gist.github.com/rwinch/cd1b459d6e04d30d72edb7e6919b3cbb) for the Spring Security-powered OAuth 2.0 handshakes.
