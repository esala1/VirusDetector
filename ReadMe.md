# Signature-based Virus Detector Using Java and PostgreSQL
## Eliyas Sala

## Description
* This Research-based project implemenets signature-based model to scan for viruses. The tools I used are below:
    * Java(Swing API for designing the GUI, event and action listeners)
    * Postgres Driver 
    * Heroku's PostgreSQL database
* The folder "Executable Application" contains the JAR application for you to open and test. When you run it, it will prompt you to choose a directory path to search for viruses. 
![](Screenshots/Screenshot1.png)
* Then, you click "Scan" button to scan for viruses in that directory. I have attached test cases in the "Dummy Files" folder containing a virus-infected batch script. Note: please do not open that file since it will crash your computer :)
![](Screenshots/Screenshot2.png)
* If the selected path is safe, the status will return clean.
![](Screenshots/Screenshot3.png)
### Heroku and Database connection
* View my code for more understanding. I used Heroku's free  add-on to access PostgreSQL database. Although my application currently recognizes around 10,000 viruses due to resource limitations, it is fully scalable for the future when more data is available and no database restrictions exist.
![](Screenshots/Screenshot5.png)
* I performed creation and insertion using "Keys.java" and "Database_Setup.java" classes. I import connection from Keys class. 
 ![](Screenshots/Screenshot4.png)
 * View my code for more understanding, thanks!

