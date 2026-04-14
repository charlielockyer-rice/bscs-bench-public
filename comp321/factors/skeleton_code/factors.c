/*
 * COMP 321 Project 1: Factors
 *
 * This program computes the number of prime factors, and optionally the number
 * of distinct prime factors, for an unsigned integer input.
 * 
 * <Put your name and NetID here>
 */

#include <assert.h>
#include <math.h>     // Included in case you use the standard math library.
#include <stdbool.h>
#include <stdio.h>
#include <unistd.h>

static unsigned int	count_distinct_factors(unsigned int n);
static unsigned int	count_factors(unsigned int n);
static unsigned int	count_factors_recursive(unsigned int n);
static void		test_factors(void);

// Replace this comment with any global variable declarations.

// Replace this comment with any helper functions.
  
/* 
 * Requires:  
 *   The input "n" must be greater than 1.
 *
 * Effects: 
 *   Returns the number of factors of the input "n".
 */
static unsigned int
count_factors_recursive(unsigned int n)
{
	// Replace this comment with your local variable declarations.
    
	assert(n > 1);
	/*
	 * Replace this comment with your code.
	 */
	return (0);
}

/* 
 * Requires:  
 *   The input "n" must be greater than 1.
 *
 * Effects: 
 *   Returns the number of factors of the input "n".
 */
static unsigned int
count_factors(unsigned int n)
{
	// Replace this comment with your local variable declarations.
    
	assert(n > 1);
	/*
	 * Replace this comment with your code. 
	 */
	return (0);
}

/* 
 * Requires:  
 *   The input "n" must be greater than 1.
 *
 * Effects: 
 *   Returns the number of distinct factors of the input "n".
 */
static unsigned int
count_distinct_factors(unsigned int n)
{
	// Replace this comment with your local variable declarations.

	assert(n > 1);
	/*
	 * Replace this comment with your code.  Also, don't forget to modify
	 * the following statement to return the actual number of distinct
	 * factors.
	 */
	return (0);
}

/* 
 * Requires:
 *   Nothing.
 *
 * Effects:
 *   Runs testing procedures.  Currently only prints a message.
 */
static void
test_factors(void)
{

	printf("Test Factors\n");
}

/* 
 * Requires:
 *   Nothing.
 *
 * Effects:
 *   If the "-t" option is specified on the command line, then test code is
 *   executed and the program exits.
 *   
 *   Otherwise, requests a number from standard input.  If the "-r" option is
 *   specified on the command line, then prints the number of prime factors
 *   that the input number has, calculated using a recursive function.
 *   Otherwise, prints the number of prime factors that the input number has
 *   and the number of those factors that are distinct using iterative
 *   functions.
 *
 *   Upon completion, the program always returns 0.
 *
 *   If the number that is input is not between 2 and the largest unsigned
 *   integer, the output of the program is undefined, but it will not crash.
 */
int
main(int argc, char **argv)
{
	unsigned int n;
	int c;
	bool recursive = false;
	bool runtests = false;

	// Parse the command line.
	while ((c = getopt(argc, argv, "rt")) != -1) {
		switch (c) {
		case 'r':             // Use recursive version.
			recursive = true;
			break;
		case 't':             // Run test procedure and exit.
			runtests = true;
			break;
		default:
			break;
		}
	}

	// If "-t" is specified, run test procedure and exit program.
	if (runtests) {
		test_factors();
		return (0);
	}

	// Get input.
	printf("Enter number:\n");
	if (scanf("%u", &n) != 1) {
		fprintf(stderr, "Input error\n");
		return (1);
	}

	// Validate the input.
	if (n < 2) {
		fprintf(stderr, "Invalid input: %u\n", n);
		return (1);
	}

	// Print results.
	if (recursive) {
		// Use recursive version.
		printf("%u has %u prime factors.\n",
		    n, count_factors_recursive(n));
	} else {
		// Use iterative versions.
		printf("%u has %u prime factors, %u of them distinct.\n",
		    n, count_factors(n), count_distinct_factors(n));
	}

	// Report no errors.
	return (0);
}
