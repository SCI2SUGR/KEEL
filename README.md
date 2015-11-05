# KEEL
KEEL (Knowledge Extraction based on Evolutionary Learning) is an open source (GPLv3) Java software tool that can be used for a large number of different knowledge data discovery tasks. KEEL provides a simple GUI based on data flow to design experiments with different datasets and computational intelligence algorithms (paying special attention to evolutionary algorithms) in order to assess the behavior of the algorithms. It contains a wide variety of classical knowledge extraction algorithms, preprocessing techniques (training set selection, feature selection, discretization, imputation methods for missing values, among others), computational intelligence based learning algorithms, hybrid models, statistical methodologies for contrasting experiments and so forth. It allows to perform a complete analysis of new computational intelligence proposals in comparison to existing ones.

Web page:  www.keel.es

KEEL description papers:

- J. Alcalá-Fdez, L. Sánchez, S. García, M.J. del Jesus, S. Ventura, J.M. Garrell, J. Otero, C. Romero, J. Bacardit, V.M. Rivas, J.C. Fernández, F. Herrera. KEEL: A Software Tool to Assess Evolutionary Algorithms to Data Mining Problems. Soft Computing 13:3 (2009) 307-318, doi: 10.1007/s00500-008-0323-y.    

- J. Alcalá-Fdez, A. Fernandez, J. Luengo, J. Derrac, S. García, L. Sánchez, F. Herrera. KEEL Data-Mining Software Tool: Data Set Repository, Integration of Algorithms and Experimental Analysis Framework. Journal of Multiple-Valued Logic and Soft Computing 17:2-3 (2011) 255-287. 

# Quick installation

Note that Java version 7 needs to be installed on your system for this to work. Depending on your computing platform you may have to download and install it separately. It is available for free from Sun GET JAVA. If you have Java already installed in your system, please, update it to the latest version if you want to use the newest KEEL versions

To execute this program, simply execute the "GraphInterKeel.jar" file. To do this, there are two ways:

Right click on the jar icon by using the navigation utility of the OS
or

Execute the command java -jar GraphInterKeel.jar if you prefer to use a shell command.
If you want to compile KEEL source code it is advisable to use the Apache Ant Tool (available for download at the The Apache Ant Project web page). The KEEL Software tool includes a "build.xml" file to be used together with ant. To compile the KEEL project (assuming you have already installed ant) you just have to type the following commands:

Execute the command ant cleanAll. This command erases previous binary files so that there aren't any conflicts with new binary builts.
Execute the command ant. This command builds the whole KEEL project binaries using the available source code.
The installation of new data sets into the application can be done by importing them through the Data Management module or the Experiments module. These modules can convert data from several formats (CVS, ARFF, plain text) to KEEL format, thus allowing the user to quickly integrate them.

If you are interested on submitting some code to the KEEL project, please contact the Webmaster Team.

