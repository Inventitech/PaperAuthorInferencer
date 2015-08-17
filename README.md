# PaperAuthorInferencer
PaperAuthorInferencer tries to infer the authors of a given paper in PDF format without looking at the author line. It can also give you the email addresses of researchers.

The format of the PDFs that it reads in is quite important. For example, it relies on references to have this style: `[SomeReference]`
It also relies on the metatags of the PDF being filled out correctly (this is a job normally done by the ACM, so make sure to obtain the paper you want to analyze through them and not e.g. the author's personal homepage), to see if a predicted author is indeed a correct guess. 

Lastly, a word of warning: In author feature extraction, there is a huge blob of magic involved (for example, assuming that an author is typically not older than 150 years, or else something went wrong). It typically works, at least for the four years of ICSE proceedings (2010-2014) that I tested this on. But do not be disappointed if it does not work out-of-the-box for your particular scenario.
