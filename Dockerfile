# Deploys the WAR that ./mvnw package builds as five Tomcat contexts. Each one is the same app with
# its own web.xml and struts-config.xml ("#" in a directory name makes a nested context path):
#
#   /tiles/before   /tiles/after   /plain/before   /plain/after   the browser example
#   /scripted                                                     plain-text pages for curl
FROM tomcat:9.0-jdk8-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY target/maxstringlen-example/ /tmp/app/
RUN cd /usr/local/tomcat/webapps \
 && cp -r /tmp/app scripted \
 && for v in tiles-before tiles-after plain-before plain-after; do \
      ctx=$(echo $v | tr '-' '#') \
      && cp -r /tmp/app "$ctx" \
      && cp "$ctx/WEB-INF/demo/web.xml" "$ctx/WEB-INF/web.xml" \
      && cp "$ctx/WEB-INF/demo/struts-config-$v.xml" "$ctx/WEB-INF/struts-config.xml"; \
    done \
 && mkdir ROOT && echo '<% response.sendRedirect("/tiles/before/"); %>' > ROOT/index.jsp \
 && rm -rf /tmp/app
