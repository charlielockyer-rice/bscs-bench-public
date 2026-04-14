---
total_points: 40
---

# Project 6: Web Proxy - Written Questions Rubric

## Section 1: HTTP Transaction Steps (10 points)

**Question:** "Describe the steps taken by your proxy to service a single HTTP transaction from beginning to end. (10-14 sentences)"

**Expected Answer:**
A complete answer should describe these steps in order:

1. **Accept connection:** The proxy accepts a client connection on its listening socket using accept().

2. **Read request:** Read the HTTP request from the client, including the request line and all headers.

3. **Parse request line:** Extract the method, URI, and HTTP version from the request line.

4. **Parse URI:** Extract the hostname, port (default 80), and path from the URI.

5. **Connect to server:** Open a connection to the target web server using the extracted hostname and port.

6. **Forward request:** Send the (possibly modified) request line and headers to the server.

7. **Read response:** Read the server's response, including status line, headers, and body.

8. **Forward response:** Send the complete response back to the client.

9. **Log transaction:** Write an entry to the access log file with timestamp, client IP, URL, and response size.

10. **Close connections:** Close connections to both client and server.

11. **Error handling:** Handle errors at each step (connection refused, timeout, malformed request).

**Rubric:**
- Full credit (10 pts): All major steps described in logical order with sufficient detail. 10-14 sentences covering accept, parse, connect, forward, log, close.
- 8-9 pts: Most steps covered, minor gaps (e.g., missing logging or error handling).
- 6-7 pts: Core steps present but missing several components or out of order.
- 4-5 pts: Basic flow described but lacks detail on parsing or forwarding.
- 2-3 pts: Very brief, missing most steps.
- 0-1 pt: Missing or only 1-2 sentences.

---

## Section 2: Request Line Modification (4 points)

**Question:** "Did you modify the first line of the request message? If so, how? (1-3 sentences)"

**Expected Answer:**
The proxy typically modifies the request line to:

1. **Convert absolute URI to relative:** Change `GET http://www.example.com/path HTTP/1.1` to `GET /path HTTP/1.0`

2. **Downgrade HTTP version:** Change HTTP/1.1 to HTTP/1.0 to avoid persistent connections and chunked encoding.

Example: "Yes, I modified the request line by extracting the path from the absolute URI and changing the version to HTTP/1.0. For example, 'GET http://example.com/page HTTP/1.1' becomes 'GET /page HTTP/1.0'."

**Rubric:**
- Full credit (4 pts): Clearly explains both URI conversion (absolute to relative) and version change, or correctly states no modification with valid justification.
- 3 pts: Mentions one modification but not the other.
- 2 pts: Vague description without specifics.
- 1 pt: Incorrect or unclear.
- 0 pts: Missing.

---

## Section 3: Header Modification (4 points)

**Question:** "Did you add/remove/modify any request headers? If so, how? (1-3 sentences)"

**Expected Answer:**
Common header modifications include:

1. **Host header:** Ensure Host header is present with the target hostname.

2. **Connection header:** Add or modify to "Connection: close" to prevent persistent connections.

3. **Proxy-Connection:** Remove or set to "close".

4. **User-Agent:** May be modified or standardized.

Example: "I ensured the Host header matched the target server's hostname, set Connection to 'close', and removed any Proxy-Connection headers."

**Rubric:**
- Full credit (4 pts): Specifically mentions Host header handling and Connection header modification.
- 3 pts: Mentions one type of header modification with explanation.
- 2 pts: Vague description ("I modified some headers").
- 1 pt: Incorrect or unclear.
- 0 pts: Missing.

---

## Section 4: Response Forwarding (4 points)

**Question:** "How did you forward the response message? (2-4 sentences)"

**Expected Answer:**
The response forwarding should describe:

1. **Reading strategy:** How the response is read (header parsing, content-length, chunked, or read until EOF).

2. **Buffering:** Whether response is buffered entirely or forwarded incrementally.

3. **Error handling:** What happens if server closes connection early or sends malformed response.

Example: "I read the response headers first to extract Content-Length. Then I forwarded the headers to the client and read/forwarded the body in chunks until Content-Length bytes were transferred. If no Content-Length was present, I read until the server closed the connection."

**Rubric:**
- Full credit (4 pts): Explains reading strategy (Content-Length vs. EOF), buffering approach, and how body is forwarded.
- 3 pts: Describes basic forwarding but missing detail on length determination.
- 2 pts: Vague ("I sent the response to the client").
- 1 pt: Incorrect or unclear.
- 0 pts: Missing.

---

## Section 5: Thread Count (6 points)

**Question:** "How many threads did your proxy use to implement concurrency? Explain how you chose this number. (3-6 sentences)"

**Expected Answer:**
Common approaches and justifications:

1. **Thread-per-connection:**
   - Creates new thread for each client connection
   - Simple to implement
   - Potential scalability issues with many connections
   - Trade-off: simplicity vs. resource usage

2. **Thread pool:**
   - Fixed number of worker threads
   - Threads pick up connections from a queue
   - Number chosen based on: CPU cores, expected load, memory constraints
   - Trade-off: bounded resources vs. potential queuing delays

3. **Pre-forked processes** (alternative to threads):
   - Fixed number of worker processes
   - Similar considerations to thread pool

Example: "I used a thread pool with 8 worker threads. I chose 8 because it matches the number of CPU cores on the test machine, allowing for parallel execution without excessive context switching. A bounded thread pool also prevents resource exhaustion under high load."

**Rubric:**
- Full credit (6 pts): States specific number, explains the design choice (thread-per-connection vs. pool), and provides reasoning for the number based on practical considerations.
- 4-5 pts: States approach and number but justification is weak.
- 2-3 pts: States a number without meaningful justification.
- 1 pt: Vague ("I used threads").
- 0 pts: Missing.

---

## Section 6: Log File Writing (4 points)

**Question:** "How did you write to the access log file? (1-2 sentences)"

**Expected Answer:**
Should describe:
- The function(s) used (fprintf, write, etc.)
- The format of log entries
- When writes occur (after each transaction)

Example: "I used fprintf to write log entries in the format 'Date: client_IP URL size'. Each entry is written immediately after the transaction completes."

**Rubric:**
- Full credit (4 pts): Specifies the I/O function used and describes when/what is written.
- 3 pts: Mentions function but lacks detail on format or timing.
- 2 pts: Vague description.
- 1 pt: Incorrect or unclear.
- 0 pts: Missing.

---

## Section 7: Log File Atomicity (4 points)

**Question:** "How do you ensure atomicity when writing to the access log file? (1-2 sentences)"

**Expected Answer:**
Atomicity can be ensured through:

1. **Mutex/lock:** Acquire a mutex before writing, release after.

2. **File locking:** Use flock() or fcntl() for file-level locking.

3. **Single write call:** Use a single write() call with a complete formatted string (relies on POSIX atomicity guarantees for small writes).

4. **Semaphore:** Use a semaphore to serialize access.

Example: "I use a pthread mutex to protect the log file. Before writing, each thread acquires the mutex, writes the complete log entry, then releases the mutex."

**Rubric:**
- Full credit (4 pts): Describes a valid synchronization mechanism (mutex, semaphore, or file lock) and how it's used.
- 3 pts: Mentions synchronization but lacks detail on implementation.
- 2 pts: Vague ("I used locking").
- 1 pt: Incorrect mechanism or misunderstanding of atomicity.
- 0 pts: Missing.

---

## Section 8: Log File Open/Close Timing (4 points)

**Question:** "When did you open/close the access log file? (2 sentences)"

**Expected Answer:**
Common approaches:

1. **Open once at startup, close at shutdown:**
   - More efficient (no repeated open/close overhead)
   - File stays open throughout proxy lifetime
   - Requires careful handling of buffering

2. **Open/close per write:**
   - Simpler but less efficient
   - Ensures data is flushed
   - Higher overhead

Example: "I opened the log file once when the proxy started and kept it open for the entire runtime. The file is closed when the proxy receives SIGINT or terminates normally."

**Rubric:**
- Full credit (4 pts): Clearly states when file is opened and closed with reasoning for the choice.
- 3 pts: States timing but no reasoning.
- 2 pts: Vague or incomplete (only mentions open or close).
- 1 pt: Incorrect or unclear.
- 0 pts: Missing.
