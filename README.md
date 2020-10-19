# Fintech Innovation Coding Test

## Checking out the code:
To check out the project we need you to clone, not fork the repo to your own Github or Bitbucket account so that your commits won't be exposed.


## Intro:
We have an API towards application developers, which returns information about all the banks which are available for the application.

The response from the API looks like this:
```json
[
  {
    "name": "Credit Sweets",
    "id": "5678"
  },
  {
    "name": "Banco de espiritu santo",
    "id": "9870"
  },
  {
    "name": "Royal Bank of Boredom",
    "id": "1234"
  }
]
```
There are two version of the API:

- `/v1/banks/all` - implementation is based on the static file, which is locally available
- `/v2/banks/all` - new version of the API, which will need to read the data from the remote servers

Both of the version need to return the same data structure.

## Challenge:
1. Add unit tests for both API versions.

2. Refactor existing code until you are satisfied with it. This can mean changing the entire architecture of the project.

3. Complete the implementation of the `/v2/banks/all` endpoint, by implementing `BanksRemoteCalls.handle(Request request, Response response)` method.
The respective configuration file is `banks-v2.json`. Implementation needs to use the data from the configuration file,
and for each bank retrieve the data from the remote URL specified. You will need to add HTTP client of your choice to the project. 
You can find the mock implementation for the remote URLs in the MockRemotes class. 

Feel free to add comments to the code to clarify the changes you are making.
