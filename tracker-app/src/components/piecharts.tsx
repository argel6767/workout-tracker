import { PieChart, Pie, Legend, Tooltip, ResponsiveContainer } from 'recharts';
import { useGetWorkoutsByMuscleGroup } from '../hooks/useGetWorkoutsByMuscleGroup';

export const WorkoutBreakdownPieChart = () => {
  const { data, isLoading, isError } = useGetWorkoutsByMuscleGroup()
  
  if (isLoading) {
    return <span className="loading loading-dots loading-xl"></span>;
  }

  if (isError) {
    return <div>Error loading analytics</div>;
  }

  if (!data || data.length === 0) {
    return <div>No data available</div>;
  }
  
  
  return (
    <main className='py-2'>
      <h2 className='text-2xl text-center'>All Workouts Broken Down by Muscle Groups</h2>
      <PieGraph data={data} dataKey="value" nameKey='key'/>
    </main>
  )
}

const generateColors = (dataLength: number): string[] => {
  const baseColors = [
    '#0088FE',
    '#00C49F',
    '#FFBB28',
    '#FF8042',
    '#8884D8',
    '#82CA9D',
    '#FFC658',
    '#FF7C7C'
  ];

  if (dataLength <= baseColors.length) {
    return baseColors.slice(0, dataLength);
  }

  const colors: string[] = [...baseColors];
  while (colors.length < dataLength) {
    const hue = (colors.length * 360) / dataLength;
    colors.push(`hsl(${hue}, 70%, 60%)`);
  }

  return colors;
}

type PieChartProps<T extends Record<string, unknown>> = {
  data: T[];
  dataKey: string;
  nameKey: string
};

const PieGraph = <T extends Record<string, unknown>>({ data, dataKey, nameKey }: PieChartProps<T>) => {
  const colors = generateColors(data.length);
  const merged = data.map((dataPoint, index) => ({
    ...dataPoint,
    fill: colors[index]
  }));
  return (
      <ResponsiveContainer width="100%" height={500}>
        <PieChart>
          <Pie
            data={merged}
            cx="50%"
            cy="50%"
            labelLine={false}
            label={({ name, value }) => `${name}: ${value}`}
            outerRadius={80}
            fill="#8884d8"
          dataKey={dataKey}
          nameKey={nameKey}
          />
          <Tooltip />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    );
}