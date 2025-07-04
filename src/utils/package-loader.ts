/**
 * 转换SwiperTables组件的数据
 * 将第一列数据转换为对象格式 {label: 第一列值, number: 第二列值}
 */

/**
 * 转换表格数据，将第一列转换为对象格式
 * @param source 原始数据源
 * @param dimensions 数据维度（列）
 * @returns 转换后的数据
 */
export function transformTableFirstColumn(source: any[], dimensions: string[]) {
  if (!source || !Array.isArray(source) || source.length === 0 || !dimensions || dimensions.length < 2) {
    return source;
  }

  // 获取第一列和第二列的字段名
  const firstColField = dimensions[0];
  const secondColField = dimensions[1];
  
  // 如果不存在第一列或第二列，直接返回原始数据
  if (!firstColField || !secondColField) {
    return source;
  }

  // 修改数据结构
  return source.map(row => {
    // 如果行为空或不是对象类型，返回原始行
    if (!row || typeof row !== 'object') {
      return row;
    }
    
    // 创建新的行对象，避免直接修改原始对象
    const newRow = { ...row };
    
    // 获取第一列和第二列的值
    const firstColValue = row[firstColField];
    
    // 如果第一列已经是对象格式且包含label和number属性，直接返回
    if (firstColValue && typeof firstColValue === 'object' && 'label' in firstColValue && 'number' in firstColValue) {
      return newRow;
    }
    
    const secondColValue = row[secondColField];
    
    // 将第一列转换为对象格式
    newRow[firstColField] = {
      label: firstColValue, // 显示用值
      number: secondColValue // 用于条件配色和平均值计算
    };
    
    return newRow;
  });
}

/**
 * 将对象格式的第一列还原为原始值
 * @param source 已转换的数据源 
 * @param dimensions 数据维度（列）
 * @returns 还原后的数据
 */
export function restoreTableFirstColumn(source: any[], dimensions: string[]) {
  if (!source || !Array.isArray(source) || source.length === 0 || !dimensions || dimensions.length === 0) {
    return source;
  }

  // 获取第一列的字段名
  const firstColField = dimensions[0];
  
  // 如果不存在第一列，直接返回原始数据
  if (!firstColField) {
    return source;
  }

  // 修改数据结构
  return source.map(row => {
    // 如果行为空或不是对象类型，返回原始行
    if (!row || typeof row !== 'object') {
      return row;
    }
    
    // 创建新的行对象，避免直接修改原始对象
    const newRow = { ...row };
    
    // 获取第一列的值
    const firstColValue = row[firstColField];
    
    // 如果第一列是对象格式且包含label属性，还原为label值
    if (firstColValue && typeof firstColValue === 'object' && 'label' in firstColValue) {
      newRow[firstColField] = firstColValue.label;
    }
    
    return newRow;
  });
} 