# Documentation

Use this directory for Markdown documentation files your team members want to be 
visible to the rest of the team. This is a great place for:

  - **User stories + clarifications from MVC3.**
  - Standards and guidelines.
  - Team meeting notes.
  - Explanations of architectural decisions (e.g. [ADRs](https://adr.github.io)).
  - Whatever else you want to be visible to the rest of the team in perpetuity.


1) Title: Display date
   Description: As a user
   I want to see what day it is at the top of the screen
   so that I know what day it is.
   Estimate: 2
   Priority: Low
   Acceptance Criteria:

Scenario 1: Date change while the user is on the app
Given the date is January 31st
And the app is open
And it is midnight
When the day changes to February 1st
Then the date on the top of the app should change to the correct date, February 1st

Scenario 2: Date change while the user doesn’t have the app open
Given the date is January 31st
And the app is closed
And it is the next day
When the user opens the app the next day
Then the date on the top of the app should change to the correct date, February 1st



2) Title: Add Goals
   Description: As a user
   I want to be able to add goals
   So that I can see what I need to complete for the day.
   Estimate: 16
   Priority: High
   Acceptance Criteria:

Scenario 1: No existing goals
Given that there are no goals in the list
And the screen says “No goals for the Day.  Click the + at the upper right to enter your Most Important Thing”
When the user clicks the plus button, and types out/taps the mic button and writes/says “Get Lettuce”
And then clicks the check mark on the bottom right
Then “Get Lettuce” should be added to the task list and displayed on the screen.
And the keyboard should disappear

Scenario 2: Goals are present in the list
Given that “Get lettuce” is the only task we have on the list
When the user clicks the plus button
And types out/taps the mic button
And writes/says “Get tomato”
Then the new goal, “Get tomato” should be added to the bottom of the list, under “Get lettuce”
And the keyboard should disappear


3) Title: Mark off completed goals (depends on 2)
   Description: As a user
   I want the app to update on my completed tasks
   So that I know what I have completed for the day.
   Estimate: 8
   Priority: Medium
   Acceptance Criteria:
   Scenario 1: User crosses off a completed goal
   Given “Get lettuce” is in the list
   And “Get lettuce” isn’t completed
   And “Get tomato” is under “Get lettuce”
   When the user taps on “Get lettuce” to mark it as completed
   Then “Get lettuce should be striked through and moved to the bottom of the list, under “Get tomato”





4) Title: Unfinished task rollover and task erasure (depends on 3, 2, 1)
   Description: As a user
   I want the app to automatically move unfinished tasks to the next day and delete tasks that are completed once I go to the next day
   So that I can finish tasks that are late and don’t worry about tasks I have already finished the next day.
   Estimate: 8
   Priority: Medium
   Acceptance Criteria:

Scenario 1:
Given I have 2 checked off goals in my list, “Get Lettuce” and “Get Tomato”
And I have no goals to rollover to the next day
When the day changes to the next day
Then the checked off goals should be deleted
And list should say that there are no tasks

Scenario 2:
Given I have “Get Tomato” as a goal AND it is not finished AND underneath it is another goal “Get Bun”  AND it is not finished
And another goal “Get Lettuce” is completed
And I have turned off my phone but the app is still open on my phone
When the day changes to the next day and I open my phone
Then “Get Lettuce” should be deleted and disappear
And  “Get Tomato” should appear as the first goal on the list
And  “Get Bun” should appear as the second goal on the list
