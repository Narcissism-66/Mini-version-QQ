const formatDateTime = (dateStr) => {
  const inputDate = new Date(dateStr);
  const today = new Date();

  // 检查是否是同一天（年、月、日是否相同）
  const isToday =
    inputDate.getFullYear() === today.getFullYear() &&
    inputDate.getMonth() === today.getMonth() &&
    inputDate.getDate() === today.getDate();

  // 格式化时分秒（补零）
  const hours = String(inputDate.getHours()).padStart(2, '0');
  const minutes = String(inputDate.getMinutes()).padStart(2, '0');
  const seconds = String(inputDate.getSeconds()).padStart(2, '0');

  if (isToday) {
    // 如果是今天，返回 "HH:mm:ss"
    return `${hours}:${minutes}:${seconds}`;
  } else {
    // 如果不是今天，返回 "YYYY/MM/DD"
    const year = inputDate.getFullYear();
    const month = String(inputDate.getMonth() + 1).padStart(2, '0');
    const day = String(inputDate.getDate()).padStart(2, '0');
    console.log(`${year}/${month}/${day}`)
    return `${year}/${month}/${day}`;
  }
};

export { formatDateTime };
