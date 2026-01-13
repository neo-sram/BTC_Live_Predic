document.addEventListener('DOMContentLoaded', () => {
  const preds = (typeof PREDS !== 'undefined') ? PREDS : [];
  const tableBody = document.querySelector('#predTable tbody');
  const summary = document.getElementById('summary');

  function render(hours) {
    tableBody.innerHTML = '';
    const n = Math.min(hours, preds.length);
    let sum = 0;
    for (let i = 0; i < n; i++) {
      const high = preds[i];
      const low = (high * 0.99).toFixed(2);
      const close = (high * 0.995).toFixed(2);
      sum += high;
      const row = `<tr><td style="text-align:left">${i+1}</td><td>${high.toFixed(2)}</td><td>${low}</td><td>${close}</td></tr>`;
      tableBody.insertAdjacentHTML('beforeend', row);
    }
    summary.textContent = n > 0 ? `Showing next ${n} hours — average predicted High: ${ (sum/n).toFixed(2) }` : 'No predictions available';
  }

  const btn12 = document.getElementById('btn12');
  const btn24 = document.getElementById('btn24');
  const btn48 = document.getElementById('btn48');
  function setActive(btn) {
    [btn12, btn24, btn48].forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
  }
  btn12.addEventListener('click', () => { setActive(btn12); render(12); });
  btn24.addEventListener('click', () => { setActive(btn24); render(24); });
  btn48.addEventListener('click', () => { setActive(btn48); render(48); });

  // initial
  setActive(btn24);
  render(24);
});
