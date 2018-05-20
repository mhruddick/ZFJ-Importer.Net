using System.Configuration;
using System.Windows;
using System.Windows.Input;

namespace ZFJImporter.WPF
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainViewModel ViewModel
        {
            get { return (MainViewModel)DataContext; }
            set { DataContext = value; }
        }

        public MainWindow()
        {
            InitializeComponent();

            KeyDown += OnKeyDown;

            ViewModel = new MainViewModel(() => UserPasswordField.Password);

            UserPasswordField.Password = ConfigurationManager.AppSettings["defaultPassword"];
        }

        private void OnKeyDown(object sender, KeyEventArgs e)
        {
            ICommand loginCommand = ViewModel?.LoginCommand;

            if (loginCommand == null) return;

            if (e.Key == Key.Return &&
                UserPasswordField.IsKeyboardFocused &&
                loginCommand.CanExecute(null))
            {
                loginCommand.Execute(null);
            }
        }

        private void OnUserPasswordChanged(object sender, RoutedEventArgs e)
        {
            ViewModel?.RaiseCanLoginChanged();
        }
    }
}