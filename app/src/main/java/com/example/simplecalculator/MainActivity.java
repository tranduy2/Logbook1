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

    private boolean isResultShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDisplay = findViewById(R.id.etDisplay);
        tvExpression = findViewById(R.id.tvExpression);

        etDisplay.setText("0");
        tvExpression.setText("");

        int[] numberIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};

        int[] operatorIds = {R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide};

        View.OnClickListener numberListener = v -> {
            Button b = (Button) v;
            String currentText = etDisplay.getText().toString();
            String inputNum = b.getText().toString();

            if (isResultShown) {
                etDisplay.setText(inputNum);
                tvExpression.setText("");
                isResultShown = false;
                return;
            }

            if (currentText.equals("0")) {
                etDisplay.setText(inputNum);
            } else {
                if (currentText.length() < 15) {
                    etDisplay.append(inputNum);
                }
            }
        };

        for (int id : numberIds) findViewById(id).setOnClickListener(numberListener);

        findViewById(R.id.btnDot).setOnClickListener(v -> {
            if (isResultShown) {
                etDisplay.setText("0.");
                tvExpression.setText("");
                isResultShown = false;
            } else if (!etDisplay.getText().toString().contains(".")) {
                etDisplay.append(".");
            }
        });

        View.OnClickListener operatorListener = v -> {
            Button b = (Button) v;
            String op = b.getText().toString();
            String currentNum = etDisplay.getText().toString();
            String history = tvExpression.getText().toString();


            if (isResultShown) {
                tvExpression.setText(currentNum + " " + op + " ");
                isResultShown = false;
            } else {
                tvExpression.setText(history + currentNum + " " + op + " ");
            }

            etDisplay.setText("0");
        };
        for (int id : operatorIds) findViewById(id).setOnClickListener(operatorListener);

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            etDisplay.setText("0");
            tvExpression.setText("");
            isResultShown = false;
        });

        findViewById(R.id.btnEqual).setOnClickListener(v -> {
            String history = tvExpression.getText().toString();
            String currentNum = etDisplay.getText().toString();

            if (history.isEmpty()) return;

            String fullExpression = history + currentNum;

            try {
                double result = evaluate(fullExpression);
                String displayResult = (result == (long) result)
                        ? String.format("%d", (long) result)
                        : String.valueOf(result);

                etDisplay.setText(displayResult);
                tvExpression.setText(fullExpression);
                isResultShown = true;

            } catch (Exception e) {
                etDisplay.setText("Error");
                tvExpression.setText("");
                isResultShown = true;
            }
        });
    }

    private double evaluate(String expression) {
        expression = expression.replaceAll(" ", "");
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