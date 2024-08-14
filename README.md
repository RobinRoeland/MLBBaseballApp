# MLBBaseballApp
Android java app for baseball.
This app was written by Robin Roeland in android studio Koala 2024.1.1 Patch 1.

Description:
- The Baseball ticket app allow users to select a Major League Baseball team from the American or National league. Users can browse the game schedule via a calendar and see the team roster of the 
  team. Team roster can be searched by name, jersey number or position. When selecting a game from the calendar, users can go to the team roster, buy a ticket for the game using paypal and find 
  an accomodation for the stay near the stadium using a search in booking.com for the game date. The ticket can only be purchased if the date is not in the past.
  When a ticket is purchased, a ticket is saved localy on the phone and a qr code is generated.
  The ticket can be seen in the ticket list fragment. When entering the stadium on game date, the ticket can be clicked to show the QR code.
  The app works off line if a team has been selected previously while being offline. Obviously as we use paypal, tickets can not be purchased when offline.
  
  The app retrieves data from the MLB official api's and shows real time MLB game dates and team data.
  When data is received, it is immediatly created/updates in the local room DB. Team Logo's and stadium images are retrieved from a website.
  
- Room DB keeps a list of leagues, MLB teams, MLB games, players for team (roster) and purchased tickets.

- if the app is online, the api calls to most recent info is made online.
  if the app is offline, the room db is used to display as much of information as is already known from last online calls.
  if a team has not been opened online, it cannot be opened offline.
  The app first checks if the mobile device is online or not. depending on the state, a flow of operations is performed in the loading activity.

- Tickets can not be purchased unless online, paypal is needed for payment confirmation.

- Stadiums have seating maps. When buying a ticket, a local assets file is used to load the seating information from the stadium. The filename is "seating_" + team abbreviation name + ".txt" and is
   located in the assets folder under app/src/main/assets folder distributed with the app. Only 2 stadiums were digitised: Milwaukee and Los Angeles. The others use a standard fake seating map (copied from Milwaukee team).
   Each stadium has a list of seating zones (VenueZone) containing a list of VenueBox objects. Each VenueBox has a number of available seats and a price in dollars.
   Already occupied seats by other users is not implemented yet but a feature that can be added in the future.

- The following Paypal test data accounts were created in Paypal sandbox mode and can be used for logging in and testing the app
    use client account id to test application when buying a ticket (sandbox mode), paying from Paypal account.
	Paypal Rest API application data:
		Client ID: AdbT4aXxgxiqs6DZi4WlhbNL2FdAJIlIwleHJRpkqrxbe66fjn6XAZLtSC91IAiDyVNtu4HcfggP-Uu9
		Secret key 1: EJxr5kQ8GO0Zf-MtJN9b-o3ILy1N_cUOH71I9s0hw4FtsPeL_UjcrDLi-SgxU0O5Qq3HOL_Wpd14M33g
	Paypal accounts
		merchant acc : sb-kghf328214221@business.example.com
		client acc : sb-vcl4n28214222@personal.example.com
			   pwd : 9)/1CGcu

- Here is an overview of the used API locations:

Part of the app is using MLB Baseball data from https://lookup-service-prod.mlb.com (teams) and games/roster are using the offical MLB site https://statsapi.mlb.com/api/v1
I found information on the first api using : https://appac.github.io/mlb-data-api-docs/#team-data-list-teams-get
I requested an official MLB API key to MLB.com but as I'm not a business I got declined for an API key and student accounts were not allowed.
I did some research on internet and found out that some of the functions were distributed online and I used those calls  making use of this documentation: https://github.com/asbeane/mlb-stats-api
Using the results of these calls in API response in firefox, I was able to reconstruct the classes that make up the response.

* Teams: 
	https://appac.github.io/mlb-data-api-docs/#team-data-list-teams-get
	call : https://lookup-service-prod.mlb.com/json/named.team_all_season.bam?sport_code=%27mlb%27&all_star_sw=%27N%27&sort_order=name_asc&season=%272023%27
* games retrieval for schedule fragment : 
	call : https://statsapi.mlb.com/api/v1/schedule/games/?sportId=1&startDate=2024-03-28&endDate=2024-09-29
* URL roster retrieval gives players that are playing for the  selected team:
	https://statsapi.mlb.com/api/v1/teams/139/roster
	where 139 is the mlb_orig_team_id of the team as received from the teams call.
* URL for retrieval online of mugshots of player heads (images based on MLBPerson Id via the  offical website of MLB)
	https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:silo:current.png/r_max/w_180,q_auto:best/v1/people/674072/headshot/silo/current
	where 674072 is the personid and can be replaced by any id from roster person id.
* URL for searching with booking.com for a room on the night of the game match in the home team location city.
	https://www.booking.com/searchresults.en-gb.html?ss=Chicago&ssne=Chicago&lang=en-gb&sb=1&src_elem=sb&dest_type=city&checkin=2024-03-13&checkout=2024-03-14&group_adults=2&no_rooms=1&group_children=0
* It was not possible to find a generic site of MLB team images so I created my own and made them available on this website :
	http://www.jursairplanefactory.com/baseballimg/team/

* PayPal Mobile Rest API for online purchasing of stadium game tickets.

	Info url : https://lo-victoria.com/the-complete-guide-to-integrate-paypal-in-mobile-apps
	
	I followed the steps in the url to implement paypal purchase in my app. the link describes quite clearly how the paypal api works.
	You need to create a transaction token. this token is a number that paypal gives you to identify calls about the same transation. a kind of session id.
	Next you need to capture the order (or create a purchase order) (we send this to  paypal to inform what your order is and how much to pay). 
	In our order, we only create a purchase for 1 single ticket. This could be improved in the future to allow multiple tickets.
	Paypal responds in it's answer with 4 URL links that can be called. (the "self" link itself, to approve, update or capture the order)
	to find the right link in the response, we search for the "rel" item in the "links" response where rel = "approve"
	We use the "approve" link in the app to confirm we want to buy the ticket.
	When calling this, paypal goes to the payment section on it's own site. This is opened using an internet browser that runs inside the app (customtabintent lib). The explanation why it's better can be found in the info url.
	The complicated part in this story was that paypal returns from the customtabintent browser to the app with succes of failure using a "deep link". 
	The deep link for cancel or success is passed with the order when sending the capture order call in the step before.
	A deep link is a url that it called and which is associated with an activity. In our case the .PaypallProcessCompleteActivity (the name of the deeplink is www.mlbbaseballrobinroeland.com)
	By this link, android know it has to open the app on the defined fragment. The link is defined in the androidmanifest.xml.
	The actitity PayPalProcessCompleteActivity handles allows to still cancel or complete the payment.
	When cancelling the ticket is not created, when confirming a qr code is generated using com.google.zxing library and the png file is stored locally as well as the ticket is created in room DB.
	After this step, the activity moves back to the mainactivity giving a hint using a stringExtra named "JumpToFragment" having either the value "AnnulerenTicket" or "TicketSuccess"
	When "Ticketsuccess" the mainactivity moves to the ticket fragment showing the new ticket.
	When "AnnulerenTicket", the mainactity returns to the calendar.
	
* Check if online
	using a call from an asynctask Task_VerifyOnlineStatus
	using url: http://statsapi.mlb.com/api/v1/conferences
	this is a small api call returning small amount of data. it it's not responding or goes to exception, the phone is not online and we store this in the data pool (MLBDataLayer).

* File disc location for stored cache of offline images:
	On mobile device in com.example.baseballapp under /images met subfolders teams, league, people, stadium, tickets
	
- Information on the implementation  choices:

* LoadActivity is used for initial loading screen and screen between teamselection and player loading.
   it works in phases : for initial loading
	- phase Online verification : checks if there is an internet connection by calling a web method on MLB api
	- this is phase I : initial load teams and team logo images
	
   secondary loading step after team selection
	- this is phase II : each time a team is selected, the loadactivity restarts and loads all games for the team and all players in the team with their mugshots

	the states are made as enums and the current state is kept.
	
	because most of the actions in the loadactivity are calls to async tasks which run in a seperate process, the progress of these if checked in a timer function from checkStatusTimer
	every 500 milliseconds, this timer is called and checks what state we are now in, and if the condition is true for moving to the next state
	for example  when phaseWaitingForReturnOnline is done, it starts reading teams with an api call and moves to the phaseLoadOne_readteams state
	if we are in state phaseLoadOne_readteams, we can only move on if the condition repo.areTeamsAvailableAndImagesRead() is true. when this is true the next step is launched which is saving to the room db and moving the state.
	
* Re-use of existing library classes or git projects:
	- LocalFileOperations 
	- ZXing : used to generate QR codes when buying a ticket for a game (implementation 'com.journeyapps:zxing-android-embedded:3.6.0')
			  (https://github.com/journeyapps/zxing-android-embedded)
	- CCT: Chrome Custom Tabs browser
		implementation("androidx.browser:browser:1.3.0")
		https://www.geeksforgeeks.org/how-to-use-custom-chrome-tabs-in-android/
	- async-http class 
		This is a class that allows calling http calls in a seperate thread without having to create the call in a specific asynctask process. the async is built in in the http caller class
		

-problem solving on different topics or info used to find information :

-HTTP call was giving a problem : "CLEARTEXT communication to www.website.com not permitted by network security policy"
	https://www.geeksforgeeks.org/android-cleartext-http-traffic-not-permitted/
-Making custom application class in android java :
	https://www.linkedin.com/pulse/application-class-android-amit-nadiger
-How to make a dialog in android : 
	https://www.youtube.com/watch?v=alV6wxrbULs
-how to make transparent dialog background
	https://stackoverflow.com/questions/10795078/dialog-with-transparent-background-in-android  -> answer 10/4/2018 20:40
-passing a parameter to an activity for the loading activity in 2 modes
	https://medium.com/@haxzie/using-intents-and-extras-to-pass-data-between-activities-android-beginners-guide-565239407ba0
	https://stackoverflow.com/questions/4233873/how-do-i-get-extra-data-from-intent-on-android
-to make calls easier and less complex without using tasks I used async-http for the  paypal transactions, found the info in the paypal info url 
    implementation("com.loopj.android:android-async-http:1.4.11")
- used information to know about deep links
	https://developer.android.com/training/app-links/deep-linking	
- I got crashes on setvisibility of a textview from a thread runnable (in loadactivity timer switch case)
    this was caused because you can only set the visibility of a textview, or change of any control in the main UI thread (or the same thread that made it)
	found via: https://stackoverflow.com/questions/11140285/how-do-we-use-runonuithread-in-android
	Solution was to use runOnUiThread call from the function (see ShowErrorMessage)
- how to sort a list in java : bubble sort (for ticket sorting on date)
	https://www.javatpoint.com/bubble-sort-in-java

Questions used ChatGPT:

"making an android java app. i have a string containing url return value. how can i load an imageview using the string url. 
  i already have the content of the bitmap in a string value which i called from a okhttpclient object and received as body of a url response. how to load this response in an imageview"
  
"if using an imageview, can i ask if a bitmap has already been set with setImageBitmap" : antwoord gebruik getDrawable() van ImageView en check null

"using an enum, how to cast the enum to int" -> .toOrdinal()

"write a function to return number of days in a month given a month and a year taking into account leapyears

vector conversion of a pathData to a vector drawable using a copied image path from a http source.

Please enjoy the app ! It was satisfying to write it.
