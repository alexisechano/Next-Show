/* 
  main.js (Deployed in Parse Cloud Code)
  Grabs currently watching show data for all users in the system
  Alexis Echano
*/

// constants
const CLASS_USER = "User";
const KEY_CURRENTLY_WATCHING = "currentlyWatching";

Parse.Cloud.define("fetchCommunity", async(request) => {
  // get query on user
  const userQuery = new Parse.Query(CLASS_USER);

  // find Users that have existing currently watching show
  userQuery.exists(KEY_CURRENTLY_WATCHING);
  userQuery.include(KEY_CURRENTLY_WATCHING);

  // find users that have a currently watching show -> chain call
  const data = await userQuery.find().then(function(users) {
    // init results array
    let results = [];

    // loop through array
    for (let i = 0; i < users.length; i++) {
      // retrieves show object from pointer in User
      let showObj = users[i].get(KEY_CURRENTLY_WATCHING);

      // check valid and then add to results
      if (showObj != null) {
        // add to show item (hash map)
        let showItem = {
          "user": users[i],
          "show": showObj
        };

        // add to results
        results.push(showItem);
      }
    }
    // return results list to outer function
    return results;
  });

  // gets data from inner query and returns to app
  return data;
});