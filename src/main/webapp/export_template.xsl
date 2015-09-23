<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/projectXML">
  <html>
  <body>
  <h2><xsl:value-of select="name"/> project export</h2>
  <table border="1">
    <tr style="background:#2c5aa0;color:white" >
      <th>Name</th>
      <th>Description</th>
	  <th>Type</th>
	  <th>Priority</th>
	  <th>Estimate</th>
	  <th>Story points</th>
	  <th>Due date</th>
    </tr>
    <xsl:for-each select="taskList/task">
    <tr>
      <td><xsl:value-of select="name"/></td>
      <td>
	  <xsl:value-of select="description" disable-output-escaping="yes" />
	  </td>
	  <td><xsl:value-of select="type"/></td>
	  <td><xsl:value-of select="priority"/></td>
	  <td><xsl:value-of select="estimate"/></td>
	  <td><xsl:value-of select="story_points"/></td>
	  <td><xsl:value-of select="due_date"/></td>
    </tr>
    </xsl:for-each>
  </table>
  </body>
  </html>
</xsl:template>
</xsl:stylesheet>