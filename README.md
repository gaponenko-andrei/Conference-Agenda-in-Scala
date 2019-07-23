# The Conference Agenda - Code Challenge

You are planning a big programming conference and have received many proposals which have passed the initial screen process but you're having trouble fitting them into the time constraints of the day -- there are so many possibilities! So you write a program to do it for you.

Here are some important details about this conference:
-	The conference has multiple tracks each of which has a morning and afternoon session.
-	Each session contains multiple talks.
-	Morning sessions begin at 9 am and must finish by 12 noon, for lunch.
-	Afternoon sessions begin at 1 pm and must finish by 5 pm for snacks.
-	No talk title has numbers in it.
-	All talk lengths are either in minutes (not hours) or lightning (5 minutes).
-	Presenters will be very punctual; there needs to be no gap between sessions.

Note that depending on how you choose to complete this problem, your solution may give a different ordering or combination of talks into tracks. This is acceptable; you don’t need to exactly duplicate the sample output given here.

Input Format:
The first line will contain an integer N. N lines follow each will contain a string.

Output Format:
For each query output the solution to the problem. Refer to the sample output for further clarification.

Constraints:
1 <= N <= 20
Every string will contain a text initially (which may contain space) followed by time of completion in minutes at the end. Refer to sample input for further clarification.

Sample Input File:
------------------
- Writing Fast Tests Using Selenium 60min
- Overdoing it in Java 45min
- AngularJS for the Masses 30min
- Ruby Errors from Mismatched Gem Versions 45min
- Common Hibernate Errors 45min
- Rails for Java Developers lightning
- Face-to-Face Communication 60min
- Domain-Driven Development 45min
- What's New With Java 11 30min
- A Perfect Sprint Planning 30min
- Pair Programming vs Noise 45min
- Java Is Not Magic 60min
- Ruby on Rails: Why We Should Move On 60min
- Clojure Ate Scala (on my project) 45min
- Programming in the Boondocks of Seattle 30min
- Ant vs. Maven vs. Gradle Build Tool for Back-End Development 30min

Sample Output:
-------------
- Track 1:
- 09:00AM Writing Fast Tests Using Selenium 60min
- 10:00AM Ruby on Rails: Why We Should Move On 60min
- 11:00AM Face-to-Face Communication 60min
- 12:00PM Lunch
- 01:00PM Java Is Not Magic 60min
- 02:00PM Clojure Ate Scala (on my project) 45min
- 02:45PM Domain-Driven Development 45min
- 03:30PM Pair Programming vs Noise 45min
- 04:15PM User Interface CSS in AngularJS Apps 30min
- 04:45PM Rails for Java Developers lightning
- 04:50PM Networking Event
- Track 2:
- 09:00AM Java Legacy App Maintenance 60min
- 10:00AM Common Hibernate Errors 45min
- 10:45AM Ruby Errors from Mismatched Gem Versions 45min
- 11:30AM AngularJS for the Masses 30min
- 12:00PM Lunch
- 01:00PM Overdoing it in Java 45min
- 01:45PM What's new with Java 11 30min
- 02:15PM A Perfect Sprint Planning 30min
- 02:45PM Programming in the Boondocks of Seattle 30min
- 03:15PM Ant vs. Maven vs. Gradle Build Tool for Back-End Development 30min
- 03:45PM A World Without HackerNews 30min
- 04:15PM Networking Event
