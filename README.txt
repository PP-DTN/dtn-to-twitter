How to setup
============
1) Import the project into Eclipse
2) Rename twitter4j.properties.sample to twitter4j.properties and enter the
   correct API keys from Twitter.
3) Launch the application from Eclipse

** Assumes IBRDTN Daemon is running on the computer. Running on Scenargie
   node requires exporting the .jar file and installing java on the node.
   The file twitter4j.properties will also need to be placed in teh same
   folder as the .jar file.

How to use
==========
The gateway is available at the "twitter" EID of the host. In this example,
the DTN node running the host will be named "gateway". The gateway will send
a reply back to the EID used to send the message. The API is in JSON.

Sending a new tweet
-------------------
# Send the request
echo '{"act":"update_status","message":"Hello World"}' | dtnsend --src twitter_client dtn://gateway/twitter
# Wait for reply
dtnrecv --name twitter_client

Getting the home timeline
-------------------------
echo '{"act":"get_home_timeline"}' | dtnsend --src twitter_client dtn://gateway/twitter
dtnrecv --name twitter_client

Searching Twitter
-----------------
echo '{"act":"search", "message":"NAIST"}' | dtnsend --src twitter_client dtn://gateway/twitter
dtnrecv --name twitter_client

License
=======
This project includes open-source softwares. 
   IBR-DTN (https://www.ibr.cs.tu-bs.de/projects/ibr-dtn/)
   Twitter4j (http://twitter4j.org/)

IBR-DTN and Twitter4j are licensed under the Apache license ver.2.0.
(http://www.apache.org/licenses/LICENSE-2.0)
