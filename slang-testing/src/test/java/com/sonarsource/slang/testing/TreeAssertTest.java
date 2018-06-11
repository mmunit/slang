/*
 * SonarSource SLang
 * Copyright (C) 2009-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonarsource.slang.testing;

import com.sonarsource.slang.api.AssignmentExpressionTree;
import com.sonarsource.slang.api.BinaryExpressionTree;
import com.sonarsource.slang.api.Comment;
import com.sonarsource.slang.api.IdentifierTree;
import com.sonarsource.slang.api.LiteralTree;
import com.sonarsource.slang.api.TextRange;
import com.sonarsource.slang.api.TreeMetaData;
import com.sonarsource.slang.impl.AssignmentExpressionTreeImpl;
import com.sonarsource.slang.impl.BinaryExpressionTreeImpl;
import com.sonarsource.slang.impl.BlockTreeImpl;
import com.sonarsource.slang.impl.IdentifierTreeImpl;
import com.sonarsource.slang.impl.LiteralTreeImpl;
import com.sonarsource.slang.impl.TextRangeImpl;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

import static com.sonarsource.slang.testing.TreeAssert.assertTree;
import static java.util.Collections.singletonList;

public class TreeAssertTest {

  private static final IdentifierTreeImpl IDENTIFIER_ABC = new IdentifierTreeImpl(null, "abc");
  private static final LiteralTreeImpl LITERAL_42 = new LiteralTreeImpl(null, "42");
  public static final AssignmentExpressionTreeImpl ASSIGN_42_TO_ABC =
    new AssignmentExpressionTreeImpl(null, AssignmentExpressionTree.Operator.EQUAL, IDENTIFIER_ABC, LITERAL_42);
  private static final BinaryExpressionTreeImpl ABC_PLUS_42 =
    new BinaryExpressionTreeImpl(null, BinaryExpressionTree.Operator.PLUS, IDENTIFIER_ABC, LITERAL_42);

  @Test
  public void identifier_ok() {
    assertTree(IDENTIFIER_ABC).isIdentifier("abc");
  }

  @Test(expected = AssertionError.class)
  public void identifier_with_wrong_name() {
    assertTree(IDENTIFIER_ABC).isIdentifier("xxx");
  }

  @Test(expected = AssertionError.class)
  public void not_an_identifier() {
    assertTree(LITERAL_42).isIdentifier("abc");
  }

  @Test
  public void literal_ok() {
    assertTree(LITERAL_42).isLiteral("42");
  }

  @Test(expected = AssertionError.class)
  public void literal_with_wrong_value() {
    assertTree(LITERAL_42).isLiteral("123");
  }

  @Test(expected = AssertionError.class)
  public void not_a_literal() {
    assertTree(new LiteralTreeImpl(null, "42")).isLiteral("123");
  }

  @Test
  public void binary_ok() {
    assertTree(ABC_PLUS_42).isBinaryExpression(BinaryExpressionTree.Operator.PLUS);
  }

  @Test(expected = AssertionError.class)
  public void binary_with_wrong_operator() {
    assertTree(ABC_PLUS_42).isBinaryExpression(BinaryExpressionTree.Operator.MINUS);
  }

  @Test(expected = AssertionError.class)
  public void not_a_binary() {
    assertTree(LITERAL_42).isBinaryExpression(BinaryExpressionTree.Operator.PLUS);
  }

  @Test
  public void assignment_ok() {
    assertTree(ASSIGN_42_TO_ABC).isAssignmentExpression(AssignmentExpressionTree.Operator.EQUAL);
  }

  @Test(expected = AssertionError.class)
  public void assignment_with_wrong_operator() {
    assertTree(ASSIGN_42_TO_ABC).isAssignmentExpression(AssignmentExpressionTree.Operator.PLUS_EQUAL);
  }

  @Test(expected = AssertionError.class)
  public void not_an_assignment() {
    assertTree(LITERAL_42).isAssignmentExpression(AssignmentExpressionTree.Operator.EQUAL);
  }

  @Test
  public void empty_block() {
    assertTree(new BlockTreeImpl(null, Collections.emptyList())).isBlock();
  }

  @Test
  public void non_empty_block() {
    assertTree(new BlockTreeImpl(null, singletonList(LITERAL_42))).isBlock(LiteralTree.class);
  }

  @Test(expected = AssertionError.class)
  public void block_with_wrong_child_class() {
    assertTree(new BlockTreeImpl(null, singletonList(LITERAL_42))).isBlock(IdentifierTree.class);
  }

  @Test(expected = AssertionError.class)
  public void block_with_too_many_children() {
    assertTree(new BlockTreeImpl(null, singletonList(LITERAL_42))).isBlock();
  }

  @Test(expected = AssertionError.class)
  public void not_a_block() {
    assertTree(LITERAL_42).isBlock(LiteralTree.class);
  }

  @Test
  public void text_range() {
    assertTree(new IdentifierTreeImpl(meta(new TextRangeImpl(1, 2, 3, 4)), "a")).hasTextRange(1, 2, 3, 4);
  }

  @Test(expected = AssertionError.class)
  public void wrong_text_range() {
    assertTree(new IdentifierTreeImpl(meta(new TextRangeImpl(1, 2, 3, 4)), "a")).hasTextRange(1, 2, 3, 42);
  }

  private TreeMetaData meta(TextRange textRange) {
    return new TreeMetaData() {
      @Override
      public TextRange textRange() {
        return textRange;
      }
      @Override
      public List<Comment> commentsInside() {
        return null;
      }
    };
  }
}