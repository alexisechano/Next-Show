NEXT SHOW - Original App Design Project
===

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
A TV show recommender app that uses personal ratings and interests to find the user's next binge!

### App Evaluation
- **Category:** Entertainment, social
- **Mobile:** Mobile first experience
- **Story:** Allows users to find their next possibly-life changing TV show and share their review with others
- **Market:** Anyone who enjoys the entertainment industry and as streaming services increase in popularity and become more prevalent, this is a helpful application to aid in putting the watcher first and helps avoid the endless scrolling of shows on Netflix, Hulu, etc.
- **Habit:** After they finish a good show, they return to the app to find a new one OR help a friend/family find the new shot show. It is habit forming and as long as a person is interested in filling up their time with TV, the app keeps runnning!
- **Scope:** In a narrow focus, this app just has a news feed with search/ilter features and a user profile. The news feed will JUST show top tv shows and ones that are similar to the ones the user selects or rates. In the future, we can add sharing and more social-network-y features.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

Outlined by FBU App Expectations and Priority
* **Your app has multiple views**
  * User can view their profile, home timeline, and saved shows and ratings in separate views (P0)
  * User can view TV show details from home timeline (P0)
* **Your app interacts with a database (e.g. Parse)**
  * User can save rated shows and shows-to-watch (P1)
  * User can view more than 5+ shows on home timeline through CardView and RecyclerView (P0)
  * User can view TV show details like synopsis (P0)
  * User ratings (like or dislike) are also saved within a database (P0)
* **You can log in/log out of your app as a user**
  * User lands on log-in screen and the app has user persistence (P0)
  * User can logout through profile page (P0)
* **You can sign up with a new user profile** 
  * User can register with new account (P0)
  * User can set up recommendations by choosing favorite genres and current fave shows (P2)
* **Your app integrates with at least one SDK (e.g. Google Maps SDK, Facebook SDK) or API (that you didn’t learn about in CodePath)**
  * User receives show recommendations on home timeline using [Trakt](https://trakt.docs.apiary.io/#reference/shows/recommended) (P1) 
* **Your app uses at least one gesture (e.g. double tap to like, e.g. pinch to scale)**
  * User can swipe to dismiss a show rec on home timeline which counts as a dislike rating but not a save (P2)
* **Your app uses at least one animation (e.g. fade in/out, e.g. animating a view growing and shrinking)**
  * In progress (P2)
* **Your app incorporates at least external library to add visual polish**
  * In progress (P2)
* **Your app provides opportunities for you to overcome difficult/ambiguous technical problems (more below)**
  * User can filter through TV shows based on Genre or if they are trending or not (P1)
  * Use combination of APIs to create own data model that has a recommendation system based on like/dislike ratings (P1)
  * App might need to do some background fetching and proper data handling to be able to show accurate, fast recommendations (P3)

**Optional Nice-to-have Stories**

* User can broadcast currently watching show on profile
* User has a nice UI experience with modern, clean design
* Add more complicated rating system like numerical rating out of 5.0
* Add season breakdown for show details and ability to rate per season
* User's news feed has friend's TV shows
* User ratings persist and are shown to friends's feeds
* User can customize profile and if they haven't watched shows, use movies as a starting point


### 2. Screen Archetypes

* Login Screen
   * User can sign-in and view their own profile
* Registration Screen
   * User can create new accountUser can sign-in and view their own profile
   * User can create profile with list of shows and favorite genres
* Timeline/Activity Feed
    * User can scroll through card-views of TV show recommendations
    * User can see top trending TV shows
    * User can search and filter through feed
    * User can swipe to dismiss show on feed (counts as a dislike)
* TV Show Detail Screen
    * User can view TV show details like season count, ratings, ranking
* Profile Screen
    * User can broadcast currently watching show
    * User can view their profile and fave genres
* Rating/Saved shows Screen
    * User can see a saved list of shows they've watched
    * User can add ratings/liked shows in separate view

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home/Timeline
* Profile
* Ratings/Saved Shows

**Flow Navigation** (Screen to Screen)

* Home Activity Feed
   * TV Show details
* Profile
   * Broadcast current show to feed (like compose tweet and publish) -> optional story
* Login screen/registration
    * Home Timeline/Activity Feed if signin
    * Profile if register
* Rating/Saved shows
  * Can see TV Show Details page similar to if on feed
  * Separate rating activity

## Wireframes
<img src="WireframeSketch-12.jpg" width=900>

### [BONUS] Digital Wireframes & Mockups
<img src="DigitalSketch-1.png" width=600>

## Schema 

### Models

**Model: Show**
| Property    | Type.       | Description |
| ----------- | ----------- | ----------- |
| objectId    | String      | Unique id for the user post (default field)       |
| user   | User Pointer    | User that saved/rated this show   |
| avgRating   | Number        |   Average rating (from API) |
| numSeasons   | Number        |   How many seasons a show has |
| network   | String        |  What network or streaming service the show is on |
| genres   | Array of Strings        | List of genres this show falls into |
| image   | File | Poster image |
| synopsis   | String | Show summary and tagline |

**Model: User**
| Property    | Type.       | Description |
| ----------- | ----------- | ----------- |
| objectId    | String      | unique id for the user post (default field)       |
| username  | String | Unique string for user handle |
| firstName  | String | String for first name |
| lastName  | String | String for last name |
| bio  | String | String for bio blurb |
| savedShows   | Array of TMDB IDs      | Shows that are already watched and rated   |
| forbiddenShows   | Array of TMDB IDs      | Shows that are disliked but not saved (gesture)  |
| faveGenres  | Array of Strings      | List of this User's favorite TV genres |
| displayShow  | Show | Currently watching or Fave Show broadcasted on profile |


### Networking
Home/Timeline
* (Read/GET) Query all recommended and trending shows with details

Login/Register
* (Read/GET) Get profile information from DB
* (Create/POST) Create new account

Profile
* (Read/GET) Query profile information from DB
* (PUT) Edit profile image, bio and genres

Ratings/Saved Shows
* (Read/GET) Query show details
* (Create/POST) Create a new rating and comment
* (Delete) Delete rating or show

[**External API: Trakt**](https://trakt.docs.apiary.io/#reference/shows)
