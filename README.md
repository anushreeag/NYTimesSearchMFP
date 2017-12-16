# NYTimesSearchMFP


**NewYorkTimes Search** is an android app that allows a user to search for articles on web using simple filters. 
The app utilizes [New York Times Search API](http://developer.nytimes.com/docs/read/article_search_api_v2).

* [x] User can **search for news article** by specifying a query and launching a search. 
Search displays a grid of image results from the New York Times Search API. - Shows Article Thumbnail and HeadLine
* [x] Used the **ActionBar SearchView** or custom layout as the query box instead of an EditText
* [x] User can tap on any article in results to view the contents in a detailed view of article.
* [x] User can **scroll down to see more articles**. The maximum number of articles is limited by the API search.
* [x] User can **share an article link** to their friends or email it to themselves

The following **optional** features are implemented:
* [x] API Query is done using RetroFit networking library.
* [x] If Thumbnail is not there for an article, thumbnail is not shown
* [x] User can click on "Filter" which allows selection of **advanced search options** to filter results
* [x] User can configure advanced search filters such as:
  * [x] Begin Date (uses a date picker)
  * [x] News desk values (Arts, Fashion & Style, Sports, Business, Weekend)
  * [x] Sort order (newest or oldest)
* [x] Subsequent searches have any filters applied to the search results
* [x] User can refresh by pulling the search result -- Pull to Refresh with the same search query and newly selected filters
* [x] Implements robust error handling, [check if internet is available]
* [x] Used vector drawables
* [x] Used Glide image loading library to load images
* [x] Used Databinding library to ensure compile time binding
* [x] Progress bar to show searching in progress
* [x] Empty query never triggers the search
* [x] Toast indicating webpage is loading and Query search is success
* [x] If no result found, displaying the message "No results found"
* [x] Search is based on network on or off. Search menu is disabled if network/wifi is off
* [x] Categories, Description are shown for every news. Different colors for different categories


Video Walkthrough of App : Link : [https://i.imgur.com/ksxbVwO.gif]
