# Nghiep vu banking: Balance, Ledger, Maker/Checker, Idempotency, Reconciliation

Tai lieu nay ghi lai cac nguyen tac nghiep vu quan trong de nang cap project demo core banking theo huong gan voi thuc te hon. Day van la ban hoc tap, khong thay the thiet ke banking production.

## 1. Ledger Balance vs Available Balance

Trong tai khoan ngan hang can phan biet hai loai so du:

- `balance`: so du thuc te, con goi la ledger balance.
- `frozen_amount`: so tien dang bi phong toa/hold.
- `available_balance`: so du kha dung ma khach hang co the dung.

Cong thuc:

```text
available_balance = balance - frozen_amount
```

Vi du:

```text
balance = 10,000,000
frozen_amount = 2,000,000
available_balance = 8,000,000
```

### Hold/Clear/Release

Voi cac giao dich chua chac chan thanh cong ngay, vi du thanh toan the hoac chuyen tien ngoai mang:

- `HOLD`: cong tien vao `frozen_amount`, lam giam available balance.
- `CLEAR`: khi giao dich thanh cong, tru tien that o `balance` va giam `frozen_amount`.
- `RELEASE`: khi giao dich that bai, giam `frozen_amount`, tra lai available balance.

Trong transfer noi bo demo, he thong co the clear ngay trong cung transaction. Tuy nhien code nen tach ham hold/clear/release de sau nay mo rong.

## 2. Double-Entry Bookkeeping va General Ledger

He thong ngan hang khong nen chi cap nhat truc tiep:

```sql
UPDATE accounts SET balance = balance - amount
```

Moi bien dong tien nen co but toan doi ung:

- Debit: ghi No.
- Credit: ghi Co.
- Tong Debit luon bang tong Credit.

Voi tai khoan tien gui khach hang, day la khoan no phai tra cua ngan hang:

- Khach hang nap tien: ngan hang tang tai san, dong thoi tang no phai tra cho khach hang.
- Khach hang chuyen/rut tien: no phai tra cua ngan hang voi khach hang giam.

### Transfer noi bo

Khach hang A chuyen 1,000,000 VND cho khach hang B:

```text
Debit  account A  1,000,000
Credit account B  1,000,000
```

Bang `general_ledger_entries` nen luu:

- `id`
- `transaction_id`
- `account_id`
- `entry_type`: `DEBIT` hoac `CREDIT`
- `amount`
- `currency`
- `description`
- `created_at`

Khi co loi sau khi da hach toan, khong sua/xoa but toan cu. Tao but toan dao `REVERSAL`.

## 3. Maker/Checker - Four-Eyes Principle

Nghiep vu rui ro cao can nguyen tac 4 mat:

- Maker: nguoi tao yeu cau/hso.
- Checker: nguoi kiem tra va phe duyet.
- Mot nguoi khong duoc tu duyet yeu cau do chinh minh tao ra.

Ap dung:

- Customer hoac staff tao KYC/loan: trang thai `PENDING`.
- Checker/Admin duyet: `APPROVED`/`VERIFIED` hoac `REJECTED`.
- RBAC can co role/permission rieng, vi du `ROLE_CHECKER`, `KYC_REVIEW`, `LOAN_APPROVE`.

Trong code nen check:

```text
if (record.createdBy == currentUser) reject
```

## 4. Idempotency Key

Idempotency dam bao mot request bi gui lap lai nhieu lan van chi xu ly mot lan.

Client gui header:

```text
Idempotency-Key: <uuid>
```

Backend:

- Kiem tra key da ton tai chua.
- Neu ton tai va da xu ly, tra lai ket qua cu.
- Neu chua ton tai, xu ly giao dich va luu key.
- Khong cho chuyen nguoc trang thai tu `SUCCESS` ve `PENDING`.

Trong demo co the luu key vao database bang cot `transactions.idempotency_key`. Production co the dung Redis hoac bang idempotency rieng co TTL/status/response snapshot.

## 5. Reconciliation va Reversal

Reconciliation la doi soat giao dich voi doi tac/he thong ngoai.

Cron job EOD hoac job lap lai:

- Quet transaction `PENDING` qua 15-30 phut.
- Goi API doi tac de lay trang thai that.
- Dong bo ve `SUCCESS`/`FAILED`.

Reversal:

- Khong `DELETE`.
- Khong sua but toan cu de che giau lich su.
- Tao transaction/but toan dao nguoc de hoan tac tac dong tai chinh.

Vi du giao dich cu:

```text
Debit  A 1,000,000
Credit B 1,000,000
```

Reversal:

```text
Debit  B 1,000,000
Credit A 1,000,000
```

## Checklist nang cap project

- Them `frozen_amount` va `available_balance`.
- Transfer check available balance thay vi chi check balance.
- Them `general_ledger_entries`.
- Moi transfer thanh cong ghi du 2 ledger entries.
- Them `idempotency_key` cho transfer.
- Them rule maker/checker cho KYC va loan.
- Them job reconciliation demo cho transaction pending.
- Them API reversal demo neu can trinh bay sau.

## Trang thai ap dung trong TPLite hien tai

Project dang code phan nen tang can demo truc tiep:

- `Account.balance`: ledger balance.
- `Account.frozenAmount`: so tien dang hold.
- `Account.getAvailableBalance()`: tinh `balance - frozenAmount`.
- `Account.hold(amount)`: phong toa tien, tang `frozenAmount`.
- `Account.clear(amount)`: tru ledger balance va giam `frozenAmount`.
- `Account.release(amount)`: giai phong hold neu giao dich fail/timeout.
- `Account.credit(amount)`: cong tien vao tai khoan thu huong.

Luon chuyen tien noi bo hien tai:

```text
lock account A/B
validate owner/status/currency/available balance
create Transaction PENDING
fromAccount.hold(amount)
fromAccount.clear(amount)
toAccount.credit(amount)
mark Transaction SUCCESS
```

Vi la internal transfer demo nen `HOLD -> CLEAR` dien ra trong cung mot `@Transactional`. Sau nay neu lam external transfer:

```text
API transfer external:
  lock account
  hold(amount)
  create Transaction PENDING
  publish Kafka message

Webhook success:
  lock account
  clear(amount)
  mark SUCCESS

Webhook failed/timeout:
  lock account
  release(amount)
  mark FAILED
```

Phan chua code full, chi can nam ly thuyet luc nay:

- General ledger double-entry.
- Reconciliation/EOD.
- Reversal transaction.
