# Cube Academy - Android Task

## Task

As part of your assessment, you are required to complete an android application with the given UI design and API document. The app will have a basic skeleton already implemented for your convenience, but there are parts of the code that needs implementation. These parts have been marked with TODO comments.

### The app will include the following features:

-	View their current month's nomination
-	Create a nomination

#### Useful links
- Designs: [Figma](https://www.figma.com/file/BAOzJacpI4IemeawyFlw5j/Mobile-Mini-Task-flow?type=design&node-id=2818-8902&mode=design&t=31N76gmtRrCVHnj9-4)
- API documentation: [OpenAPI docs](https://cube-academy-api.cubeapis.com/docs)

### API requests

Please read the [OpenAPI docs](https://cube-academy-api.cubeapis.com/docs) documentation before beginning.

You need to include an Authorization header to your API calls after you successfully register and login the user. The API uses a Bearer token for Authorization.

** Since we don't have a login page, you need to call the register/login endpoint from a software like postman and store the returned Auth Token in the gradle.properties. We have left an empty variable in there for you **

### Your contribution
As stated earlier, parts of the code that require your implementation are marked with a TODO comment. These parts include:
- On the first screen:
  - You should populate the data of the nominations recycler view from the data taken from the api. (You should know that for new users, the data fetched from the api will be empty and therefore the empty screen should be shown. But after you create new nominations, these should show up in the list)
- On the second screen:
  - You should add the input controllers to the screen's xml file. Again, the boilerplate part of the ui has already been added, and parts of the xml file that requires your code has been marked with TODO comments. You need to make sure that your implementation matches the given figma designs. 
  - You should add logic to the controllers in the activity class
  - You should use the api to send the user's input and create a new nomination
- On the third screen:
  - You should add action logic to the two action buttons at the bottom of the screen. One should start another "Create nomination" form and the second should exit to the first screen. 
- As part of the logic of the code, the basic DI modules and the retrofit instance and endpoints have already been defined, but they are not being used. 
  - You should update the [Repository] class to work with the API.

### What do we expect from the app? 
- The app should demonstrate good use of Kotlin technologies. 
- We're looking for organised, clean, readable and documented code. Make sure you structure the files cleanly and define the classes and functions in a readable way. 
- Try to add comments about your decisions. Communicate with us about your choices and reasoning. 
- Before reviewing the code, we start and test the app. So make sure you do enough testing to be confident of the app's performance. 
- Good use of Android resources (themes, dimens, styles, strings, colors, etc.)
- Good use of the skeleton structure already defined
- Use git

### Important note
You are responsible for providing a clean and efficient answer. 
- If you need to add new models, new files, new helper functions, etc. you are allowed to do so. 
- If you think what we have done isn't the best approach, you can let us know with a comment. 
- If you feel the need to change a part of the code that was given to you for any reason, please explain why in your comments.  

### Bonus
#### The following points are bonus tasks. They’re not necessary, but we would love to see your take on them. 
- Automated testing - We have put some empty test functions in the Instrumented testing module with TODO comments. If you have time, please try to add some good tests in those functions.  
- Create a text document and write up other features that you think this app could use. What have we missed? Where have we gone wrong in our idea? What else would be useful? You can come up with ideas around the UX of the app, the API that we use, other methods in voting, logic issues, future problems or bugs, constraints, etc. 
- Error handling - Try to implement a good error handling method for different possible errors the user might see from API errors to network or any other possible ones 
- Make sure your code has the least amount of warnings and errors in the logical, and also in the IDE (No unused variables, unused imports, obsolete code, etc.)

### The task will be marked on the following aspects:
-	Technical ability - How good the code quality is?
-	Attention to detail - How close does it look to the designs?
-	Organisational skills - How clean and readable the code is? 
-	Overall solution - does it accomplish the task and how well?

## Submission
For submission, fork this repository and send us the link to your git repository to [cubeacademy@3sidedcube.com](mailto:cubeacademy@3sidedcube.com?subject=Cube%20Academy%20Test)
(If you don't like to use github, you can simply clone this project and copy the files into your favorite repository and give us the url to that. Just make sure we have access to it.)

## Questions?

If you have any questions about any of the above please get in touch with us at [cubeacademy@3sidedcube.com](mailto:cubeacademy@3sidedcube.com?subject=Cube%20Academy%20Test)


