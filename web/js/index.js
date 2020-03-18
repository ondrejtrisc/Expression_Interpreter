class Example {
  constructor(id, instruction, expression) {
    this.id = id;
    this.instruction = instruction;
    this.expression = expression;
  }

  render = () => {
    this.element = document.createElement('div');
    this.element.className = 'example';
    this.element.innerHTML = `<div class="instruction">
                                  ${this.instruction}:
                              </div>
                              <div id="console${this.id}">
                                  <form action="javascript:main(document.forms[${this.id}].elements[0].value, ${this.id});">
                                      <input type="text" size="110" value="${this.expression}">
                                      <button type="submit">&crarr;</button>
                                  </form>
                              </div>
                              <div id="display${this.id}"></div>`;
    document.getElementById('app').appendChild(this.element);
  }
}

document.addEventListener('DOMContentLoaded', () => {

  const example0 = new Example(0, 'Add numbers', '+(12, 34.56)');
  example0.render();
  const example1 = new Example(1, 'Subtract numbers', '-(12.34, 56)');
  example1.render();
  const example2 = new Example(2, 'Multiply numbers', '*(-2, 3, -4)');
  example2.render();
  const example3 = new Example(3, 'Divide numbers', '/(3, -2)');
  example3.render();
  const example4 = new Example(4, 'Combine arithmetical operations into complex expressions', '+(-(1, 2), *(+(3.4, 5, 6), -(7, 8), *(9.1, 2)), /(/(3, 4), 5))');
  example4.render();
  const example5 = new Example(5, 'Negate boolean values', '!(true)');
  example5.render();
  const example6 = new Example(6, 'Conjoin boolean values', '&(true, false, true)');
  example6.render();
  const example7 = new Example(7, 'Disjoin boolean values', '||(false, true, false)');
  example7.render();
  const example8 = new Example(8, 'Combine logical operations into complex expressions', '||(&(true, false, true), !(!(true)), false, &(true, ||(false, !(false))))');
  example8.render();
  const example9 = new Example(9, 'Compare values', '=(2, 3)');
  example9.render();
  const example10 = new Example(10, 'Build conditions', 'if(=(2, 3), equal, unequal)');
  example10.render();
  const example11 = new Example(11, 'Build your own functions', '{fahrenheit. *(-(fahrenheit, 32), /(5, 9))}(451)');
  example11.render();
  const example12 = new Example(12, 'Build your own higher-order functions', '{f. f(2)}({y. +(y, 3)})');
  example12.render();
  const example13 = new Example(13, 'Return functions as values', '{x, y. +(x, y)}(2)');
  example13.render();
  const example14 = new Example(14, 'Write recursive algorithms with the use of the Y combinator', '[{f. {x. f(x(x))}({x. f(x(x))})}({factorial, n. if(=(n, 0), 1, *(n, factorial(-(n, 1))))})](5)');
  example14.render();
  const example15 = new Example(15, 'Experiment', '');
  example15.render();
});

