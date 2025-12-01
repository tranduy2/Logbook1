package com.example.simplecalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private EditText etDisplay;
    private TextView tvExpression;
    private StringBuilder input = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDisplay = findViewById(R.id.etDisplay);
        tvExpression = findViewById(R.id.tvExpression);

        int[] numberIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot};

        int[] operatorIds = {R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide};

        View.OnClickListener numberListener = v -> {
            if (input.length() >= 15) {
                return;
            }

            Button b = (Button) v;
            input.append(b.getText());
            tvExpression.setText(input.toString());
        };

        for (int id : numberIds) findViewById(id).setOnClickListener(numberListener);

        View.OnClickListener operatorListener = v -> {
            Button b = (Button) v;
            if (input.length() > 0) {
                char lastChar = input.charAt(input.length() - 1);

                if ("+-×÷".indexOf(lastChar) == -1) {
                    if (input.length() >= 15) {
                        return;
                    }
                    input.append(b.getText());
                    tvExpression.setText(input.toString());
                }
            }
        };
        for (int id : operatorIds) findViewById(id).setOnClickListener(operatorListener);

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            input.setLength(0);
            tvExpression.setText("");
            etDisplay.setText("0");
        });

        findViewById(R.id.btnEqual).setOnClickListener(v -> {
            try {
                String expression = input.toString();
                if (expression.isEmpty()) return;
                double result = evaluate(expression);

                String displayResult = (result == (long) result)
                        ? String.format("%d", (long) result)
                        : String.valueOf(result);

                etDisplay.setText(displayResult);
                tvExpression.setText(expression);
                input.setLength(0);
                input.append(displayResult);
            } catch (Exception e) {
                etDisplay.setText("Error");
                tvExpression.setText("");
                input.setLength(0);
            }
        });
    }

    private double evaluate(String expression) {
        expression = expression.replace('×', '*').replace('÷', '/');
        Stack<Double> numbers = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--;
                numbers.push(Double.parseDouble(sb.toString()));
            }
            else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(ch)) {
                    numbers.push(applyOp(ops.pop(), numbers.pop(), numbers.pop()));
                }
                ops.push(ch);
            }
        }

        while (!ops.isEmpty()) {
            numbers.push(applyOp(ops.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return 0;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }
}