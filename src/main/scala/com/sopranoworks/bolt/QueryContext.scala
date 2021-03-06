/**
  * Bolt
  * QueryContext
  *
  * Copyright (c) 2017 Osamu Takahashi
  *
  * This software is released under the MIT License.
  * http://opensource.org/licenses/mit-license.php
  *
  * @author Osamu Takahashi
  */
package com.sopranoworks.bolt

import com.sopranoworks.bolt.Bolt.Nut
import com.sopranoworks.bolt.values._

case class QueryContext(nut:Nut, parent:QueryContext) {
  private var _aliases = Map.empty[String,Alias]
  private var _resultAliases = Map.empty[String,QueryResultAlias]
  private var _currentTableName:Option[String] = None
  private var _currentTable:Option[Table] = None
  private var _identifiers = Map.empty[String,IdentifierValue]

  private var _subquery:Option[SubqueryValue] = None

  def addAlias(alias:Alias):Unit = {
    if (_aliases.contains(alias.name))
      throw new RuntimeException(s"Alias '${alias.name}' is duplicated")
    _aliases += (alias.name->alias)
  }
  def getAlias(name:String):Option[Alias] = _aliases.get(name) //.orElse(Option(parent).flatMap(_.getAlias(name)))

  def addResultAlias(alias:QueryResultAlias):Unit = {
    if (_resultAliases.contains(alias.name))
      throw new RuntimeException(s"Alias '${alias.name}' is duplicated")
    _resultAliases += (alias.name->alias)
  }

  def setCurrentTable(tableName:String):Unit = _currentTableName = Some(tableName)
  def currentTable:Option[String] = _currentTableName

  def setSubquery(subquery:SubqueryValue):Unit  = {
    _subquery = Some(subquery)
    
  }
  def subquery = _subquery

  def identifier(name:String):IdentifierValue =
    _identifiers.getOrElse(name, {
      val idt = IdentifierValue(name, this)
      _identifiers += (name -> idt)
      idt
    })
}
