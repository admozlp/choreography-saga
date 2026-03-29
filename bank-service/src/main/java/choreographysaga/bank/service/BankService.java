package choreographysaga.bank.service;

import choreographysaga.common.dto.BankResponse;
import choreographysaga.common.dto.StartPaymentRequest;
import choreographysaga.common.exception.OperationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BankService {

    public BankResponse startPayment(StartPaymentRequest request) {
        String html = generateHtml(request);

        BankResponse bankResponse = new BankResponse(html, request.paymentId().toString(), "a6d96d946dc505ae50cba4ec5170cda9");

        double percent = Math.random() * 100;
        if (percent <= 10) {
            int[] simulatedCodes = {400, 404, 409, 422, 500, 502, 503, 504};
            int httpStatusCode = simulatedCodes[(int) (Math.random() * simulatedCodes.length)];
            throw new OperationException("Simulated error with status code: " + httpStatusCode, HttpStatus.valueOf(httpStatusCode));
        }

        return bankResponse;
    }

    private static String generateHtml(StartPaymentRequest request) {
        String html = """
                <!DOCTYPE html>
                <html lang="tr">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                  <title>3D Secure Doğrulama</title>
                  <style>
                    * { box-sizing: border-box; margin: 0; padding: 0; }
                    body {
                      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
                      background: #f0f2f5;
                      min-height: 100vh;
                      display: flex;
                      align-items: center;
                      justify-content: center;
                      padding: 24px;
                    }
                    .card {
                      background: #fff;
                      border-radius: 12px;
                      padding: 32px 28px;
                      width: 100%;
                      max-width: 380px;
                      box-shadow: 0 2px 16px rgba(0,0,0,0.10);
                    }
                    .bank-name {
                      font-size: 13px;
                      font-weight: 600;
                      color: #1a3a6b;
                      text-transform: uppercase;
                      letter-spacing: 0.5px;
                      margin-bottom: 20px;
                    }
                    .amount-row {
                      display: flex;
                      justify-content: space-between;
                      align-items: baseline;
                      padding: 14px 0;
                      border-top: 1px solid #eee;
                      border-bottom: 1px solid #eee;
                      margin-bottom: 24px;
                    }
                    .merchant { font-size: 14px; color: #555; }
                    .amount   { font-size: 22px; font-weight: 700; color: #1a1f2e; }
                    h2 { font-size: 15px; font-weight: 600; color: #1a1f2e; margin-bottom: 4px; }
                    .subtitle { font-size: 13px; color: #888; margin-bottom: 20px; }
                    .otp-inputs { display: grid; grid-template-columns: repeat(6, 1fr); gap: 8px; margin-bottom: 12px; }
                    .otp-inputs input {
                      width: 100%;
                      height: 52px;
                      border: 1.5px solid #ddd;
                      border-radius: 8px;
                      text-align: center;
                      font-size: 22px;
                      font-weight: 600;
                      color: #1a1f2e;
                      outline: none;
                    }
                    .otp-inputs input:focus { border-color: #1a3a6b; }
                    .timer { font-size: 12px; color: #888; margin-bottom: 20px; }
                    .timer span { color: #e8700a; font-weight: 600; }
                    .btn { width: 100%; padding: 13px; border: none; border-radius: 8px; font-size: 15px; font-weight: 600; cursor: pointer; }
                    .btn-primary { background: #1a3a6b; color: #fff; margin-bottom: 10px; }
                    .btn-cancel  { background: #f5f5f5; color: #555; font-weight: 400; }
                  </style>
                </head>
                <body>
                <div class="card">
                  <div class="bank-name">SIM Bank · 3D Secure</div>
                  <input type="hidden" id="paymentId" value="${PAYMENT_ID}" />
                
                  <div class="amount-row">
                    <span class="merchant">${MERCHANT_NAME}</span>
                    <span class="amount">${CURRENCY_SYMBOL}${AMOUNT}</span>
                  </div>
                
                  <h2>SMS Doğrulama Kodu</h2>
                  <p class="subtitle">${MASKED_PHONE} numarasına gönderildi</p>
                
                  <div class="otp-inputs">
                    <input type="text" maxlength="1" inputmode="numeric" id="d0" />
                    <input type="text" maxlength="1" inputmode="numeric" id="d1" />
                    <input type="text" maxlength="1" inputmode="numeric" id="d2" />
                    <input type="text" maxlength="1" inputmode="numeric" id="d3" />
                    <input type="text" maxlength="1" inputmode="numeric" id="d4" />
                    <input type="text" maxlength="1" inputmode="numeric" id="d5" />
                  </div>
                
                  <div class="timer">Kodun geçerlilik süresi: <span id="timer">02:00</span></div>
                
                  <button class="btn btn-primary" onclick="verify()">Onayla</button>
                  <button class="btn btn-cancel" onclick="cancel()">İptal</button>
                </div>
                
                <script>
                  const OTP = '${OTP_CODE}';
                  const inputs = Array.from(document.querySelectorAll('.otp-inputs input'));
                  let seconds = 120;
                
                  inputs.forEach((inp, i) => {
                    inp.addEventListener('input', () => {
                      inp.value = inp.value.replace(/\\D/g, '');
                      if (inp.value && i < 5) inputs[i + 1].focus();
                    });
                    inp.addEventListener('keydown', e => {
                      if (e.key === 'Backspace' && !inp.value && i > 0) {
                        inputs[i - 1].value = '';
                        inputs[i - 1].focus();
                      }
                    });
                  });
                
                  const t = setInterval(() => {
                    seconds--;
                    const m = String(Math.floor(seconds / 60)).padStart(2, '0');
                    const s = String(seconds % 60).padStart(2, '0');
                    document.getElementById('timer').textContent = m + ':' + s;
                    if (seconds <= 0) { clearInterval(t); document.getElementById('timer').textContent = 'Suresi doldu'; }
                  }, 1000);
                
                  async function verify() {
                    const entered = inputs.map(i => i.value).join('');
                    if (entered.length < 6) return alert('Lutfen 6 haneli kodu giriniz.');
                    if (entered === OTP) {
                      try {
                        const res = await fetch('http://localhost:3530/payments/callback', {
                          method: 'POST',
                          headers: { 'Content-Type': 'application/json' },
                          body: JSON.stringify({ status: 'success', otp: entered, paymentId: document.getElementById('paymentId').value })
                        });
                        if (res.ok) {
                          alert('Odeme onaylandi!');
                        } else {
                          alert('Sunucu hatasi: ' + res.status);
                        }
                      } catch (e) {
                        alert('Baglanti hatasi: ' + e.message);
                      }
                    } else {
                      alert('Hatali kod. Lutfen tekrar deneyiniz.');
                      inputs.forEach(i => { i.value = ''; });
                      inputs[0].focus();
                    }
                  }
                
                  function cancel() {
                    if (confirm('Odemeyi iptal etmek istiyor musunuz?')) alert('Islem iptal edildi.');
                  }
                
                  inputs[0].focus();
                </script>
                </body>
                </html>
                """;

        return html
                .replace("${MERCHANT_NAME}", "Adem Özalp A.Ş")
                .replace("${CURRENCY_SYMBOL}", "₺")
                .replace("${AMOUNT}", request.amount().toPlainString())
                .replace("${MASKED_PHONE}", "+90 5** *** ** 47")
                .replace("${OTP_CODE}", "123456")
                .replace("${PAYMENT_ID}", request.paymentId().toString());
    }
}
