NEXT SHOW - Original App Design Project
===

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
A TV show recommender app that uses personal ratings and interests to aggregate a user's next binge!

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Entertainment, social
- **Mobile:** Mobile first experience
- **Story:** Allows users to find their next possibly-life changing TV show and share their review with others
- **Market:** Anyone who enjoys the entertainment industry and as streaming services increase in popularity and become more prevalent, this is a helpful application to aid in putting the watcher first and helps avoid the endless scrolling of shows on Netflix, Hulu, etc.
- **Habit:** After they finish a good show, they return to the app to find a new one OR help a friend/family find the new shot show. It is habit forming and as long as a person is interested in filling up their time with TV, the app keeps runnning!
- **Scope:** In a narrow focus, this app just has a news feed with search/ilter features and a user profile. The news feed will JUST show top tv shows and ones that are similar to the ones the user selects or rates. In the future, we can add sharing and more social-network-y features.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories** -> More single user oriented

* User can scroll through card-views of TV show recommendations
* User can see top trending TV shows
* User can search and filter through feed
* User can sign-in and view their own profile
* User profile has list of tv shows and favorite genres
* User can rate shows (not necessarily saved and only like/dislike for now)
* User can broadcast currently watching show on profile
* User can see a saved list of shows they've watched
* User can manually add ratings/liked shows in separate view
* User can create new account
* User can view TV show details like season count, ratings, ranking (show only for overall show, not per season)
* User can swipe to dismiss show on feed (counts as a dislike)

**Optional Nice-to-have Stories** -> Adding social network aspect

* More complicated rating system
* User's news feed has friend's TV shows
* Add season breakdown for show details and ability to rate per season
* User ratings persist and are shown to friends's feeds
* User can customize profile and if they haven't watched shows, use movies as a starting point
* User can share TV shows outside of the app
* User has a nice UI experience with modern, clean design


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
[Add picture of your hand sketched wireframes in this section]
<img src="WireframeSketch-12.jpg" width=900>

### [BONUS] Digital Wireframes & Mockups
<img src="DigitalSketch-1.png" width=600>

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
