<h1>SCALAble Nmap</h1>
<h3>The first network mapper fully designed in Scala.</h3>

<img src="screenshot.png" alt="screenshot.png"/>

<h2>USAGE</h2>
<p>Open sbt console and type:
<br><strong>run IP_Address</strong> or <strong>run net_ID/CIDR</strong>
<br>
Options:
    <br><strong>-h</strong> to show usage (this option won't start the scan)
    <br><strong>-open</strong> to show open ports
    <br><strong>-serv</strong> to show services running on open ports
    <br><strong>-os</strong> to detect operating systems
    <br><strong>-save</strong> to save scan results on file
    <br><strong>-v</strong> to turn on the verbose mode
    <br>---------Default options are all false---------
</p>

<h3>High scalability</h3>
<p>It's possible to add functionalities easily, exploiting the functional nature of Scala.
<br>Check the ResultsManager source code to know how to implement your changes.</p>