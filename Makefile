#include ../../../Makefile.common
include ../Makefile.common
#
# Specfic options
#
FITS_NAME	=fits
PACKAGEDIR 	=org/$(ESTAR_NAME)/$(FITS_NAME)
PACKAGENAME	=org.$(ESTAR_NAME).$(FITS_NAME)
JAR_FILE	=org_$(ESTAR_NAME)_$(FITS_NAME).jar
JAVACFLAGS 	= -d $(LIBDIR) -sourcepath ../../../ -classpath $(LIBDIR):$(CLASSPATH)
DOCSDIR 	= $(ESTAR_DOC_HOME)/javadocs/$(PACKAGEDIR)

SRCS = FITSException.java FITSHeaderParser.java FITSHeaderLoader.java FITSImage.java
OBJS = $(SRCS:%.java=$(LIBDIR)/$(PACKAGEDIR)/%.class)
DOCS = $(SRCS:%.java=$(DOCSDIR)/$(PACKAGEDIR)/%.html)
CONFIGS = jfits_environment.csh
CONFIGSBIN = $(CONFIGS:%=$(LIBDIR)/%)

DIRS = 
#test

top: jar configs
#	@for i in $(DIRS); \
#	do \
#		(echo making in $$i...; cd $$i; $(MAKE) ); \
#	done;

$(LIBDIR)/$(PACKAGEDIR)/%.class: %.java
	$(JAVAC) $(JAVAC_OPTIONS) $(JAVACFLAGS) $<
jar: $(JARLIBDIR)/$(JAR_FILE)

$(JARLIBDIR)/$(JAR_FILE): $(OBJS)
	(cd $(LIBDIR); $(JAR) $(JAR_OPTIONS) $(JAR_FILE) $(PACKAGEDIR); $(MV) $(JAR_FILE) $(JARLIBDIR))

docs: $(DOCS)
#	@for i in $(DIRS); \
#	do \
#		(echo docs in $$i...; cd $$i; $(CO) $(CO_OPTIONS) Makefile; $(MAKE) docs); \
#	done;

$(DOCSDIR)/$(PACKAGEDIR)/%.html: %.java
	$(JAVADOC) -sourcepath ../../..:$(CLASSPATH) -d $(DOCSDIR) $(DOCFLAGS) $(PACKAGENAME)

configs: $(CONFIGSBIN)

$(LIBDIR)/%: %
	$(CP) $< $@

checkout:
	$(CO) $(CO_OPTIONS) $(SRCS)
#	@for i in $(DIRS); \
#	do \
#		(echo checkout in $$i...; cd $$i; $(CO) $(CO_OPTIONS) Makefile; $(MAKE) checkout); \
#	done;

checkin:
	-$(CI) $(CI_OPTIONS) $(SRCS)
#	-@for i in $(DIRS); \
#	do \
#		(echo checkin in $$i...; cd $$i; $(MAKE) checkin; $(CI) $(CI_OPTIONS) Makefile); \
#	done;

depend:
	echo "No depend."

clean:
	-$(RM) $(RM_OPTIONS) $(OBJS) $(TIDY_OPTIONS)
#	@for i in $(DIRS); \
#	do \
#		(echo cleaning in $$i...; cd $$i; $(MAKE) clean); \
#	done;

tidy:
	-$(RM) $(RM_OPTIONS) $(TIDY_OPTIONS)
#	@for i in $(DIRS); \
#	do \
#		(echo cleaning in $$i...; cd $$i; $(MAKE) tidy); \
#	done;

backup: tidy checkin
	-$(RM) $(RM_OPTIONS) $(OBJS)
#	@for i in $(DIRS); \
#	do \
#		(echo backup in $$i...; cd $$i; $(MAKE) backup); \
#	done;
	$(TAR) cvf $(BACKUP_DIR)/org_$(ESTAR_NAME)_$(FITS_NAME).tar .
	$(COMPRESS) $(BACKUP_DIR)/org_$(ESTAR_NAME)_$(FITS_NAME).tar
