# OwlTest: Automated Feedback and Assessment

### Testing

It is very important to test your programs as you write them. You should carefully think about what you are trying to accomplish and write tests to make sure your program does what you expect. For the assignments in this course, that means that you should run your functions with a variety of inputs as you develop them and make sure they return the results you would expect. You shouldn't just try arbitrary inputs. You should think about where things might go wrong and try those inputs. Once you believe your function behaves in a reasonable manner, you are ready to submit it to the automated grader for feedback and assessment.
### OwlTest

OwlTest is a tool that provides a preliminary grade and feedback on your program. When you submit to OwlTest, there is no identifying information tying the submission to you. So, it is an anonymous way for you to run the automated grader on your code so that you can get feedback about how well your program runs. In each assignment description, there is an OwlTest link that will take you to a web page where you can submit your code. Note that each link is different, so you must follow the link for the assignment you are working on. Once there, you simply paste a CodeSkulptor URL into the form and click the "Submit to OwlTest" button.

OwlTest is not particularly fast, so you should not use it instead of your own testing. However, it does run a suite of tests on all of your code to determine how well your code works. Each function is tested independently, so you will receive feedback for each function. This allows you to submit to OwlTest even if you haven't completed the entire assignment.

The OwlTest page tells you which functions will be tested and how many points each function is worth towards your final grade. For each graded function, OwlTest will either print nothing (meaning you passed all of the tests!) or it will print a line telling you how many points you lost for that function and giving you a sample test that you failed. For example, imagine you are writing a simple function that takes a month and a year and returns the number of days in that month. You might receive the following feedback from OwlTest:

${\verb|[-15.0 pts] days_in_month(1, 1) expected 31 but received 0|}$

You can interpret this as follows:
- **[-15.0 pts]**You lost 15 points on this function.
- **days_in_month(1, 1)**This is the test that was run. You can copy and paste this code directly into your Python file to run the same test!
- **expected 31**This was the expected output of the test (there are 31 days in January of year 1)
- **but received 0**This is what your code returned (the submitted function was broken and returned 0)

If you have more than one function that lost points, there will be multiple lines like this. Note that they might be in different orders each time you submit to OwlTest. The tests are run in an arbitrary order and can be different each time.

The best way to proceed after losing points in OwlTest is to cut and paste the test (in this case $\color{red}{\verb|days_in_month(1, 1)|}$) directly into your Python file and run it. Then, you can work on your code until it returns the expected value.

OwlTest uses[Pylint](https://www.pylint.org/)to check that you have followed the coding style guidelines for this class. Deviations from these style guidelines will result in deductions from your final score. Please read the feedback from Pylint closely. If you have questions, feel free to consult the[guide to common Pylint errors](https://canvas.rice.edu/courses/33582/pages/guide-to-pylint-errors)or ask for help. If you violate the coding style guidelines, you will see a deduction for style. For example,

${\verb|[-1.0 pts] 1 style warnings found (maximum allowed: 20 style warnings)|}$

This particular message indicates that you lost 1 point for code style. Click on the other tabs of the output to get more details about the style warnings.

You total score for the submission is shown at the top of the output, as follows:

**Score:**${\verb|52.3/100|}$

Remember, OwlTest is*anonymous*. Therefore, this grade is not (and can not be!) reported to Canvas. This is just the score you would receive if you were to submit that file to Canvas. Once you are happy with your score, you should submit your file to Canvas.
### Canvas LTITest

Canvas LTITest formally grades your submission and submits your grade to Canvas. It operates just like OwlTest, but you must access it by opening an "LTI Tool" in Canvas. This passes your identity to the grader. After grading your submission, the grade is then sent back to Canvas so that it can be recorded.

You can always tell whether you are using OwlTest or Canvas LTITest by the color of the background. OwlTest has a*pale yellow*background. Canvas LTITest has a*white*background.

To get to Canvas LTITest, you must go to the Python assignment link within the course. That page will remind you to submit both your Python code and your writeup. At the bottom of the page is a button that allows you to load the assignment in a new window. That button will take you to the Canvas LTITest page (with a white background). Canvas LTITest then operates just like OwlTest. You submit your code and it gives you the same feedback. But, it also submits your score to Canvas.

You may submit more than once. Each time you submit to Canvas LTITest, you should go back to the assignment within Canvas and re-open the tool. Do not just refresh the page or otherwise navigate to the URL. Sometimes it takes a while for Canvas to register your grade. Be patient. Do not submit multiple times right before the deadline. You are only increasing the chances that your final submission will be registered late and you will lose points.