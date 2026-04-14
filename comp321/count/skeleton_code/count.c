/*
 * COMP 321 Project 2: Word Count
 *
 * This program counts the number of characters, words, and lines in files.
 *
 * <Put your name and NetID here>
 */

#include <ctype.h>
#include <stdarg.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

/* Structure to hold counts for a file */
struct counts {
    long chars;
    long words;
    long lines;
};

/* Function prototypes */
static int do_count(char *input_files[], const int nfiles,
    const bool char_flag, const bool word_flag,
    const bool line_flag, const bool test_flag);
static void print_counts(FILE *fp, struct counts *cnts,
    const char *name, const bool char_flag,
    const bool word_flag, const bool line_flag);
static void app_error_fmt(char *fmt, ...);

/*
 * Requires:
 *   "fp" is a valid output file stream.
 *   "cnts" is a valid pointer to a counts structure.
 *   "name" is a valid string.
 *
 * Effects:
 *   Prints the counts to fp in the format:
 *   [lines] [words] [chars] name
 *   Each count is omitted if the corresponding flag is false.
 */
static void
print_counts(FILE *fp, struct counts *cnts, const char *name,
    const bool char_flag, const bool word_flag, const bool line_flag)
{
    if (line_flag)
        fprintf(fp, "%8ld", cnts->lines);
    if (word_flag)
        fprintf(fp, "%8ld", cnts->words);
    if (char_flag)
        fprintf(fp, "%8ld", cnts->chars);
    fprintf(fp, " %s\n", name);
}

/*
 * Requires:
 *   "fmt" is a valid printf format string.
 *
 * Effects:
 *   Prints an error message to stderr in the format:
 *   ERROR: <formatted message>
 *   Does not terminate the program.
 */
__attribute__((unused))
static void
app_error_fmt(char *fmt, ...)
{
    va_list ap;
    fprintf(stderr, "ERROR: ");
    va_start(ap, fmt);
    vfprintf(stderr, fmt, ap);
    va_end(ap);
    fprintf(stderr, "\n");
}

/*
 * Requires:
 *   "input_files" is a valid array of file name strings.
 *   "nfiles" is the number of files in the array.
 *
 * Effects:
 *   Counts characters, words, and/or lines in each file based on flags.
 *   Prints results for each file in ASCIIbetical order.
 *   Prints totals at the end.
 *   Returns 0 on success, 1-255 on error.
 */
static int
do_count(char *input_files[], const int nfiles,
    const bool char_flag, const bool word_flag,
    const bool line_flag, const bool test_flag)
{
    (void)test_flag; /* Suppress unused parameter warning */

    /*
     * TODO: Implement this function.
     *
     * 1. For each file in input_files (sorted ASCIIbetically):
     *    - Open and read the file
     *    - Count chars, words, and lines as requested by flags
     *    - Print the counts using print_counts()
     *    - Handle errors with app_error_fmt()
     *
     * 2. Print the total counts at the end
     */

    /* Placeholder - print zero counts for each file */
    int error_flag = 0;
    struct counts total = {0, 0, 0};

    for (int i = 0; i < nfiles; i++) {
        struct counts file_counts = {0, 0, 0};
        print_counts(stdout, &file_counts, input_files[i],
            char_flag, word_flag, line_flag);
    }

    /* Print totals (always print all three counts) */
    print_counts(stdout, &total, "total", true, true, true);

    return error_flag;
}

/*
 * Requires:
 *   Nothing.
 *
 * Effects:
 *   Parses command line arguments and calls do_count.
 *   Returns the value returned by do_count.
 */
int
main(int argc, char **argv)
{
    int c;
    bool char_flag = false;
    bool word_flag = false;
    bool line_flag = false;
    bool test_flag = false;

    /* Parse command line options */
    while ((c = getopt(argc, argv, "cltwh")) != -1) {
        switch (c) {
        case 'c':
            char_flag = true;
            break;
        case 'l':
            line_flag = true;
            break;
        case 't':
            test_flag = true;
            break;
        case 'w':
            word_flag = true;
            break;
        case 'h':
            printf("Usage: %s [-c] [-l] [-t] [-w] <files>\n", argv[0]);
            return 0;
        default:
            fprintf(stderr, "Usage: %s [-c] [-l] [-t] [-w] <files>\n",
                argv[0]);
            return 1;
        }
    }

    /* Check for at least one input file */
    if (optind >= argc) {
        fprintf(stderr, "Error: No input files specified\n");
        return 1;
    }

    /* Get the input files */
    int nfiles = argc - optind;
    char **input_files = &argv[optind];

    return do_count(input_files, nfiles, char_flag, word_flag,
        line_flag, test_flag);
}
