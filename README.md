<h1>SCALA NMAP</h1>
<h3>The first network mapper fully designed in Scala</h3>

<h2>COMPONENTS</h2>
<h3>Main</h3>
<p>It takes two or more arguments. 
<br>The first one should be a specific IP address or something like NetID/CIDR,
to tell the scanner "Hey, I wanna discover which hosts are active in this subnet".
<br>The second one (and possibly the others) should be an option, such as: 
<br><strong>-H</strong> : to show the help message
<br><strong>-O</strong> : to show open ports of active hosts
<br><strong>-S</strong> : to save the scan results on file
<br><strong>-V</strong> : to turn on the verbose mode
<br>Through a <strong>Regex</strong>, this script automatically recognizes the format of the first argument.
<br>If it's a <strong>single IP address</strong>, it calls the <strong>Host Scanner</strong> to ping that host and check if it's active or not.
<br>If it's a <strong>combination of a NetID and a CIDR</strong>, it calls the <strong>Parser</strong> and 
it will calculate the subnet range. Finally, through the <strong>Host Scanner</strong>, it will ping all hosts in that range.
<br>Also in this case, you'll receive a report about active hosts (if there're some), through the <strong>Report Manager</strong>.
It's also able to print the report on a .txt file. 
</p>
<h3>Parser</h3>
<p>It's called by the <strong>Main</strong> and it has two very important functions. It:
<br>- parses the input, to get the NetID and the subnet mask.
Then, these two outputs are sent to the <strong>Host Scanner</strong> to procede with the scan; 
<br>- parses the flags given as arguments, to check which options the user wants to turn on. 
This options configuration is used by all files.</p>
<h3>Host Scanner</h3>
<p></p>


