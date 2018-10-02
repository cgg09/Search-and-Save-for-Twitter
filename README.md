# Search & Save for Twitter

This project consists in a desktop application that connects to the Twitter API to download tweets based on a specific search.

In this documented are presented the main functionalities of this project:

* User login

First of all the program will ensure a secure connection between the application and the Twitter API in behalf of the user: to do that, the program uses the OAuth protocol; the implementation of OAuth in Twitter is based on the *Client Credentials Grant* flow of OAuth 2.0 framework.

When the user runs the program for the first time it will show a *Login view*, in which the user will be able to *sign up* with the **New user** button, or to *sign in* with the **Existing user** button.

- In case the user is not registered in the application, by clicking the first button the program will generate the access tokens necessaries so the application can connect to the Twitter API in behalf of the user, and then it will show the main search view.

- In case the user is registered, by clicking the second button the user will be able to select its username to log in (it is displayed a list of all the users that are registered in the application).

* Search window

Once the user enters in the application, it will be shown the *Search view*, in which the user will be able to do the desired searches. This window has the following features:

- A left table, where is contained a *History search* with all the searches done by the user sorted by date. When you left-click a search it are displayed all the downloaded tweets in the right table; also, if you right-click a search it is displayed a menu with 3 options: repeat, save and delete a search (to repeat an existing search, to export the search and to delete it).
- A right table, where is contained the *Current search*: this is where the user can check an specific search from the previous table and see all the downloaded tweets. When you double-click a specific tweet it is displayed in the browser from Twitter webpage.

Also it contains the following buttons:

- On the top left corner, there is the *User settings menu*, in which the user can exit his/her session, or can also delete its username of the database.
- On the top right corner, there is a button that allows the user to filter the contents of the *Current search*: it can show the last 200 tweets downloaded, show all the tweets that are not retweets, or show all the tweets of the search.
- On the bottom letf corner, there is the button **New search...** which opens a window where the user just needs to write the query of his/her search.
- On the bottom right corner, there is the button **Save as...** to export the search that is displayed in the *Current search*.
