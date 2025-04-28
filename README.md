<h1>SCALA NMAP</h1>
<h3>The first network mapper fully designed in Scala</h3>

<img src="screenshot.png" alt="screenshot.png"/>

<h2>USAGE</h2>
<p>Open sbt console and type:
<br><strong>run IP_Address</strong>
<br>or
<br><strong>run net_ID/CIDR</strong>
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

<h2>COMPONENTS</h2>
<h3>Main:</h3>
<p>It takes two or more arguments. 
<br>The first one should be a specific IP address or something like NetID/CIDR,
to tell the scanner "Hey, I wanna discover which hosts are active in this subnet".
<br>The second one (and possibly the others) should be an option.
<br>Through a <strong>Regex</strong>, this script automatically recognizes the format of the first argument.
<br>If it's a <strong>single IP address</strong>, it calls the <strong>Host Scanner</strong> to ping that host and check if it's active or not.
<br>If it's a <strong>combination of a NetID and a CIDR</strong>, it calls the <strong>Host Scanner</strong> again 
and the subnet range will be calculated through the <strong>Parser</strong>. Finally, it will ping all hosts in that range.
<br>Also in this case, you'll receive a report about active hosts (if there're some), through the <strong>Report Manager</strong>.
It's also able to print the report on a .txt file. 
</p>

<h3>Host Scanner:</h3>
<p>It scans a single host or an entire subnet.
<br>Through a ping, the scanner will be able to discover active hosts and print them all using the <strong>Report Manager</strong> methods.
<br>It also uses the <strong>Parser</strong> to know what to do and the <strong>Ports Scanner</strong> to get infos from open ports.</p>

<h3>Ports Scanner:</h3>
<p>It sends the <strong>Host Scanner</strong> infos about ports of active hosts, using a list of common ports.</p>

<h3>Service Recon:</h3>
<p>It tries to recognize services on open ports, using a list of common services.</p>

<h3>Parser:</h3>
<p>It's called by the <strong>Main</strong> and it has two very important functions. It:
<br>- parses the input, to get the IP address or the NetID and the subnet mask.
Then, these two outputs are sent to the <strong>Host Scanner</strong> to procede with the scan; 
<br>- parses the flags given as arguments, to check which options the user wants to turn on. 
This options configuration is used by all files.</p>

<h3>Report Manager:</h3>
<p>It's used by everyone else and it's useful to print infos and save them on a .txt file.</p>

<h3>Logger:</h3>
<p>It's a simple message printer. It uses different colours for success, warning and error messages.</p>



