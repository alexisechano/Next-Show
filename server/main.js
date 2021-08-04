/* 
  main.js (Deployed in Parse Cloud Code)
  Grabs currently watching show data for all users in the system
  Alexis Echano
*/

// constants
const CLASS_USER = "User";
const KEY_CURRENTLY_WATCHING = "currentlyWatching";

Parse.Cloud.define("fetchCommunity", async(request) => {
  const userQuery = new Parse.Query(CLASS_USER);

  userQuery.exists(KEY_CURRENTLY_WATCHING);
  userQuery.include(KEY_CURRENTLY_WATCHING);

  // find users that have a currently watching show -> chain call
  const data = await userQuery.find().then(function(users) {
    let results = [];

    for (let user in users) {
      // retrieves show object from pointer in User
      let showObj = user.get(KEY_CURRENTLY_WATCHING);

      if (showObj != null) {
        let showItem = {
          "user": user,
          "show": showObj
        };

        results.push(showItem);
      }
    }
    return results;
  });

  return data;
});